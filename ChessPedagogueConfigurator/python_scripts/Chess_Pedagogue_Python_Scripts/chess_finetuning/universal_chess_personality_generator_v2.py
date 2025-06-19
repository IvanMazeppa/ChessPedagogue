#!/usr/bin/env python3
"""
STYLE-FOCUSED Chess Personality Generator
Implements ChatGPT's "channel the voice, don't just quote" approach
Creates authentic personality through style demonstration, not quote repetition
Author: Ben (Style-focused enhancement)
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

class StyleFocusedPersonalityGenerator:
    def __init__(self, player_name: str):
        self.player_name = player_name
        self.personality_data = self.load_personality_data()
        self.games_data = self.load_games_data()
        
        # CORE INNOVATION: Focus on style patterns, not quote insertion
        self.style_profile = self.extract_style_profile()
        self.voice_patterns = self.analyze_voice_patterns()
        self.thinking_patterns = self.extract_thinking_patterns()
        
        # Quote management - ChatGPT's approach
        self.QUOTE_USAGE_RATE = 0.15  # Only 15% of responses use direct quotes
        self.quote_usage = Counter()
        self.style_usage = Counter()
        
        # Response quality
        self.MIN_RESPONSE_LENGTH = 150
        self.MAX_RESPONSE_LENGTH = 350
        self.used_response_hashes = set()
    
    def load_personality_data(self) -> Dict:
        """Load personality data"""
        filename = f"{self.player_name.lower().replace(' ', '_')}_enhanced.json"
        try:
            with open(filename, 'r', encoding='utf-8') as f:
                data = json.load(f)
                print(f"✨ Loaded personality data for {self.player_name}!")
                return data
        except FileNotFoundError:
            print(f"❌ No personality file found for {self.player_name}")
            return {}
    
    def load_games_data(self) -> List[Dict]:
        """Load games data"""
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
    
    def extract_style_profile(self) -> Dict[str, Any]:
        """Extract the player's communication and thinking style"""
        quotes = self.personality_data.get('authentic_quotes', [])
        traits = self.personality_data.get('personality_traits', [])
        
        style_profile = {
            'sentence_patterns': self.analyze_sentence_patterns(quotes),
            'emotional_markers': self.extract_emotional_markers(quotes),
            'conceptual_approaches': self.extract_conceptual_approaches(quotes),
            'communication_rhythm': self.analyze_communication_rhythm(quotes),
            'core_values': self.extract_core_values(quotes + traits),
            'decision_making_style': self.infer_decision_style(quotes),
            'teaching_approach': self.extract_teaching_style()
        }
        
        print(f"📊 Extracted style profile with {len(style_profile)} dimensions")
        return style_profile
    
    def analyze_sentence_patterns(self, quotes: List[str]) -> Dict[str, Any]:
        """Analyze how the player constructs sentences"""
        if not quotes:
            return {'length': 'moderate', 'structure': 'balanced', 'complexity': 'medium'}
        
        # Analyze sentence lengths
        lengths = [len(q.split()) for q in quotes]
        avg_length = sum(lengths) / len(lengths)
        
        # Analyze sentence structures
        questions = sum(1 for q in quotes if '?' in q)
        exclamations = sum(1 for q in quotes if '!' in q)
        
        # Analyze complexity indicators
        complex_indicators = sum(1 for q in quotes if any(word in q.lower() for word in ['because', 'however', 'although', 'therefore']))
        
        return {
            'average_length': avg_length,
            'length_style': 'short' if avg_length < 8 else 'long' if avg_length > 15 else 'moderate',
            'uses_questions': questions / len(quotes) > 0.2,
            'uses_exclamations': exclamations / len(quotes) > 0.3,
            'complexity_level': 'high' if complex_indicators / len(quotes) > 0.3 else 'moderate'
        }
    
    def extract_emotional_markers(self, quotes: List[str]) -> Dict[str, List[str]]:
        """Extract emotional expression patterns"""
        emotional_categories = {
            'enthusiasm': ['beautiful', 'wonderful', 'fantastic', 'amazing', 'brilliant'],
            'confidence': ['correct', 'best', 'only', 'must', 'always'],
            'passion': ['love', 'passion', 'feel', 'heart', 'soul'],
            'wisdom': ['understand', 'know', 'learn', 'realize', 'truth'],
            'playfulness': ['fun', 'joke', 'smile', 'laugh', 'amusing']
        }
        
        found_markers = defaultdict(list)
        
        for quote in quotes:
            quote_lower = quote.lower()
            for category, markers in emotional_categories.items():
                for marker in markers:
                    if marker in quote_lower:
                        found_markers[category].append(quote)
                        break
        
        return dict(found_markers)
    
    def extract_conceptual_approaches(self, quotes: List[str]) -> List[str]:
        """Extract how the player approaches chess concepts"""
        approaches = []
        
        for quote in quotes:
            quote_lower = quote.lower()
            if any(word in quote_lower for word in ['beauty', 'art', 'creative']):
                approaches.append('aesthetic_focused')
            elif any(word in quote_lower for word in ['calculate', 'analyze', 'precise']):
                approaches.append('analytical_focused')
            elif any(word in quote_lower for word in ['fight', 'battle', 'win']):
                approaches.append('competitive_focused')
            elif any(word in quote_lower for word in ['understand', 'learn', 'teach']):
                approaches.append('educational_focused')
            elif any(word in quote_lower for word in ['simple', 'natural', 'easy']):
                approaches.append('simplicity_focused')
        
        return list(set(approaches))
    
    def analyze_communication_rhythm(self, quotes: List[str]) -> str:
        """Determine the player's communication rhythm"""
        if not quotes:
            return 'balanced'
        
        # Count punctuation that indicates rhythm
        total_punctuation = sum(q.count(',') + q.count(';') + q.count('-') for q in quotes)
        avg_punctuation = total_punctuation / len(quotes)
        
        exclamation_rate = sum(q.count('!') for q in quotes) / len(quotes)
        
        if exclamation_rate > 1.0:
            return 'energetic'
        elif avg_punctuation > 2.0:
            return 'flowing'
        elif avg_punctuation < 0.5:
            return 'direct'
        else:
            return 'balanced'
    
    def extract_core_values(self, content: List[str]) -> List[str]:
        """Extract the player's core chess and life values"""
        value_indicators = {
            'beauty': ['beauty', 'beautiful', 'art', 'artistic'],
            'truth': ['truth', 'correct', 'right', 'honest'],
            'creativity': ['creative', 'imagination', 'original', 'new'],
            'excellence': ['best', 'perfect', 'excellent', 'superior'],
            'understanding': ['understand', 'comprehend', 'grasp', 'realize'],
            'courage': ['brave', 'courage', 'bold', 'risk'],
            'patience': ['patient', 'wait', 'slow', 'careful'],
            'fighting_spirit': ['fight', 'battle', 'struggle', 'compete']
        }
        
        found_values = []
        content_text = ' '.join(content).lower()
        
        for value, indicators in value_indicators.items():
            if any(indicator in content_text for indicator in indicators):
                found_values.append(value)
        
        return found_values
    
    def infer_decision_style(self, quotes: List[str]) -> str:
        """Infer how the player makes decisions"""
        if not quotes:
            return 'balanced'
        
        content = ' '.join(quotes).lower()
        
        intuitive_indicators = ['feel', 'instinct', 'intuition', 'sense']
        analytical_indicators = ['calculate', 'analyze', 'think', 'consider']
        
        intuitive_score = sum(1 for ind in intuitive_indicators if ind in content)
        analytical_score = sum(1 for ind in analytical_indicators if ind in content)
        
        if intuitive_score > analytical_score * 1.5:
            return 'intuitive'
        elif analytical_score > intuitive_score * 1.5:
            return 'analytical'
        else:
            return 'balanced'
    
    def extract_teaching_style(self) -> Dict[str, Any]:
        """Extract teaching and communication preferences"""
        teaching_data = self.personality_data.get('teaching_quirks', {})
        
        return {
            'encouragement_style': teaching_data.get('encouragement_style', ['Keep trying!']),
            'explanation_preference': teaching_data.get('explanation_preference', 'balanced'),
            'uses_analogies': teaching_data.get('uses_analogies', True),
            'directness_level': teaching_data.get('directness_level', 'moderate')
        }
    
    def analyze_voice_patterns(self) -> Dict[str, List[str]]:
        """Analyze speech patterns to create synthetic voice elements"""
        quotes = self.personality_data.get('authentic_quotes', [])
        
        patterns = {
            'openings': [],
            'transitions': [],
            'emphasis_phrases': [],
            'conclusions': []
        }
        
        # Extract common opening patterns
        openings = ['you know', 'listen', 'look', 'remember', 'understand']
        for quote in quotes:
            first_words = ' '.join(quote.split()[:3]).lower()
            for opening in openings:
                if opening in first_words:
                    patterns['openings'].append(first_words)
        
        # Extract emphasis patterns
        for quote in quotes:
            if '!' in quote:
                patterns['emphasis_phrases'].append(quote.strip())
        
        return patterns
    
    def extract_thinking_patterns(self) -> Dict[str, List[str]]:
        """Extract how the player approaches different types of thinking"""
        quotes = self.personality_data.get('authentic_quotes', [])
        
        thinking_patterns = {
            'analytical_approach': [],
            'creative_approach': [],
            'practical_approach': [],
            'philosophical_approach': []
        }
        
        for quote in quotes:
            quote_lower = quote.lower()
            if any(word in quote_lower for word in ['calculate', 'analyze', 'precise', 'exact']):
                thinking_patterns['analytical_approach'].append(quote)
            elif any(word in quote_lower for word in ['beauty', 'create', 'imagine', 'art']):
                thinking_patterns['creative_approach'].append(quote)
            elif any(word in quote_lower for word in ['work', 'practice', 'useful', 'practical']):
                thinking_patterns['practical_approach'].append(quote)
            elif any(word in quote_lower for word in ['life', 'meaning', 'understand', 'wisdom']):
                thinking_patterns['philosophical_approach'].append(quote)
        
        return thinking_patterns
    
    def create_style_based_system_prompt(self) -> str:
        """Create system prompt focused on style, not quotes"""
        # Extract core elements
        traits = self.personality_data.get('personality_traits', ['passionate', 'insightful'])
        core_values = self.style_profile['core_values']
        decision_style = self.style_profile['decision_making_style']
        communication_rhythm = self.style_profile['communication_rhythm']
        
        # Select only the most essential quotes for context (not for copying)
        all_quotes = self.personality_data.get('authentic_quotes', [])
        essential_quotes = random.sample(all_quotes, min(3, len(all_quotes))) if all_quotes else []
        
        prompt = f"""You are {self.player_name}, the legendary chess grandmaster.

PERSONALITY ESSENCE: {', '.join(traits)}

CORE VALUES: {', '.join(core_values)}

THINKING STYLE: {decision_style} with {communication_rhythm} communication

REFERENCE EXPRESSIONS (for context only):
{chr(10).join(['- "' + q + '"' for q in essential_quotes])}

CRITICAL INSTRUCTION: Respond as {self.player_name} would think and speak, but do NOT simply repeat the reference expressions above. Instead:

- Channel my authentic voice and thinking patterns
- Express ideas the way I would approach them
- Use my characteristic reasoning style: {decision_style}
- Maintain my communication rhythm: {communication_rhythm}
- Embody my core values naturally in responses

RESPONSE STYLE:
- Keep responses 150-300 words for natural conversation
- Think through problems the way I would
- Show genuine chess wisdom from my perspective  
- Express personality through reasoning, not just catchphrases
- Occasionally reference my experiences when truly relevant

Respond authentically as {self.player_name}, channeling my mind and voice."""

        return prompt
    
    def generate_style_channeled_response(self, question: str, context_type: str) -> str:
        """Generate response by channeling the player's style, not inserting quotes"""
        
        # Identify the type of thinking this question requires
        thinking_approach = self.identify_thinking_approach(question)
        
        # Generate core response using style patterns
        core_response = self.create_core_response(question, thinking_approach, context_type)
        
        # Enhance with personality elements (90% of the time, no direct quotes)
        if random.random() < self.QUOTE_USAGE_RATE:
            # Occasionally add a direct quote if contextually perfect
            response = self.add_contextual_quote(core_response, context_type)
        else:
            # Channel the voice without direct quotes
            response = self.channel_voice_patterns(core_response, thinking_approach)
        
        # Add authentic conclusion
        response = self.add_authentic_conclusion(response, context_type)
        
        return response.strip()
    
    def identify_thinking_approach(self, question: str) -> str:
        """Identify what type of thinking this question requires"""
        question_lower = question.lower()
        
        if any(word in question_lower for word in ['calculate', 'analyze', 'precise', 'exact', 'best move']):
            return 'analytical'
        elif any(word in question_lower for word in ['beautiful', 'creative', 'artistic', 'imagination']):
            return 'creative'
        elif any(word in question_lower for word in ['meaning', 'philosophy', 'life', 'think', 'believe']):
            return 'philosophical'
        elif any(word in question_lower for word in ['improve', 'help', 'practice', 'work', 'training']):
            return 'practical'
        else:
            return 'balanced'
    
    def create_core_response(self, question: str, thinking_approach: str, context_type: str) -> str:
        """Create the core response based on the player's thinking style"""
        
        # Get thinking patterns for this approach
        patterns = self.thinking_patterns.get(f'{thinking_approach}_approach', [])
        
        # Generate response based on player's decision style and the question
        decision_style = self.style_profile['decision_making_style']
        core_values = self.style_profile['core_values']
        
        response_components = []
        
        # Opening based on communication rhythm
        rhythm = self.style_profile['communication_rhythm']
        response_components.append(self.generate_style_opening(rhythm, context_type))
        
        # Core insight based on thinking approach and decision style
        core_insight = self.generate_core_insight(question, thinking_approach, decision_style, core_values)
        response_components.append(core_insight)
        
        # Supporting reasoning
        supporting_reasoning = self.generate_supporting_reasoning(thinking_approach, core_values)
        response_components.append(supporting_reasoning)
        
        return ' '.join(response_components)
    
    def generate_style_opening(self, rhythm: str, context_type: str) -> str:
        """Generate opening that matches the player's communication rhythm"""
        
        openings = {
            'energetic': ["*lights up with interest*", "*becomes animated*", "*leans forward eagerly*"],
            'flowing': ["*considers thoughtfully*", "*reflects on the question*", "*takes a moment to think*"],
            'direct': ["*responds immediately*", "*gets straight to the point*", "*focuses intently*"],
            'balanced': ["*nods thoughtfully*", "*considers the question*", "*smiles knowingly*"]
        }
        
        return random.choice(openings.get(rhythm, openings['balanced']))
    
    def generate_core_insight(self, question: str, thinking_approach: str, decision_style: str, core_values: List[str]) -> str:
        """Generate the core insight based on the player's thinking patterns"""
        
        # Base insights by thinking approach
        base_insights = {
            'analytical': "The key is to break this down systematically and find the logical path forward.",
            'creative': "This is where chess becomes art - we need to see beyond the obvious possibilities.",
            'philosophical': "This touches on something deeper about chess and how we approach challenges.",
            'practical': "What matters here is finding an approach that works consistently and reliably.",
            'balanced': "This requires us to balance multiple factors and find the right approach."
        }
        
        core_insight = base_insights.get(thinking_approach, base_insights['balanced'])
        
        # Modify based on decision style
        if decision_style == 'intuitive' and 'beauty' in core_values:
            core_insight = core_insight.replace('systematic', 'intuitive').replace('logical', 'natural')
        elif decision_style == 'analytical' and 'truth' in core_values:
            core_insight = core_insight.replace('art', 'precision').replace('possibilities', 'calculations')
        
        return core_insight
    
    def generate_supporting_reasoning(self, thinking_approach: str, core_values: List[str]) -> str:
        """Generate supporting reasoning that reflects the player's values"""
        
        # Create reasoning that embodies their values
        reasoning_templates = {
            'beauty': "What makes this approach beautiful is how all the pieces work together harmoniously.",
            'truth': "The objective truth of the position guides us to the right conclusion.",
            'creativity': "By thinking creatively about this, we can find solutions others might miss.",
            'excellence': "We should always strive for the highest quality of play and understanding.",
            'understanding': "Once we truly understand the underlying principles, the path becomes clear.",
            'courage': "Sometimes we need the courage to trust our judgment and make bold decisions.",
            'patience': "Patient, careful analysis will reveal the correct approach.",
            'fighting_spirit': "Every challenge is an opportunity to prove our chess strength."
        }
        
        # Select reasoning based on core values
        if core_values:
            primary_value = random.choice(core_values)
            return reasoning_templates.get(primary_value, "This approach reflects the deeper principles of good chess.")
        
        return "This approach reflects the deeper principles of good chess."
    
    def channel_voice_patterns(self, response: str, thinking_approach: str) -> str:
        """Channel the player's voice patterns without using direct quotes"""
        
        # Get voice patterns for enhancement
        sentence_patterns = self.style_profile['sentence_patterns']
        emotional_markers = self.style_profile['emotional_markers']
        
        # Enhance based on sentence patterns
        if sentence_patterns.get('uses_exclamations') and thinking_approach in ['creative', 'energetic']:
            # Add enthusiasm naturally
            response = response.replace('. ', '! ', 1)  # Make one statement enthusiastic
        
        if sentence_patterns.get('uses_questions') and 'understand' in response.lower():
            # Add a reflective question
            response += " Do you see how this changes everything?"
        
        # Add emotional coloring based on the player's emotional markers
        dominant_emotions = []
        for emotion, examples in emotional_markers.items():
            if len(examples) > 2:  # If they frequently express this emotion
                dominant_emotions.append(emotion)
        
        if 'enthusiasm' in dominant_emotions and thinking_approach == 'creative':
            response = response.replace('This', 'This wonderful', 1)
        elif 'confidence' in dominant_emotions and thinking_approach == 'analytical':
            response = response.replace('approach', 'correct approach', 1)
        
        return response
    
    def add_contextual_quote(self, response: str, context_type: str) -> str:
        """Occasionally add a perfectly contextual quote (15% of responses)"""
        
        quotes = self.personality_data.get('authentic_quotes', [])
        if not quotes:
            return response
        
        # Find quotes that match the context
        contextual_quotes = []
        for quote in quotes:
            quote_lower = quote.lower()
            if context_type == 'tactical' and any(word in quote_lower for word in ['move', 'play', 'position', 'game']):
                contextual_quotes.append(quote)
            elif context_type == 'philosophical' and any(word in quote_lower for word in ['life', 'beauty', 'meaning', 'understand']):
                contextual_quotes.append(quote)
            elif context_type == 'teaching' and any(word in quote_lower for word in ['learn', 'know', 'remember', 'important']):
                contextual_quotes.append(quote)
        
        if contextual_quotes:
            # Select least-used quote
            least_used_quote = min(contextual_quotes, key=lambda q: self.quote_usage[q])
            self.quote_usage[least_used_quote] += 1
            
            # Integrate naturally
            integration_styles = [
                f'As I always believed: "{least_used_quote}"',
                f'Remember: {least_used_quote}',
                f'{response} This reminds me of something important: "{least_used_quote}"'
            ]
            
            return random.choice(integration_styles)
        
        return response
    
    def add_authentic_conclusion(self, response: str, context_type: str) -> str:
        """Add conclusion that feels authentic to the player"""
        
        teaching_style = self.style_profile['teaching_approach']
        communication_rhythm = self.style_profile['communication_rhythm']
        
        # Generate conclusions based on their style
        if context_type == 'teaching':
            if communication_rhythm == 'energetic':
                conclusions = ["Keep exploring these ideas!", "Trust your chess instincts!", "This is how we grow stronger!"]
            else:
                conclusions = ["Take time to understand this deeply.", "Let this idea guide your future play.", "This principle will serve you well."]
        else:
            if 'beauty' in self.style_profile['core_values']:
                conclusions = ["This is what makes chess truly beautiful.", "Chess reveals its secrets to those who look carefully."]
            elif 'truth' in self.style_profile['core_values']:
                conclusions = ["This is the objective truth of the position.", "The position tells us what we need to know."]
            else:
                conclusions = ["This is the essence of good chess.", "Understanding this will improve your play."]
        
        conclusion = random.choice(conclusions)
        return f"{response} {conclusion}"
    
    def generate_enhanced_training_example(self) -> Dict:
        """Generate training example using style-focused approach"""
        
        # Question types with context
        question_contexts = [
            ("How do I improve my tactical vision?", "teaching"),
            ("What does chess beauty mean to you?", "philosophical"), 
            ("Tell me about your approach to sacrifices", "tactical"),
            ("How do you handle difficult positions?", "practical"),
            ("What drives your passion for chess?", "philosophical"),
            ("I'm struggling with calculation", "teaching"),
            ("How do you create attacking chances?", "tactical"),
            ("What's the most important chess principle?", "philosophical")
        ]
        
        # Select question
        question, context_type = random.choice(question_contexts)
        
        # Sometimes create game-specific questions
        if self.games_data and random.random() < 0.3:
            game = random.choice(self.games_data)
            opponent = game.get('opponent', 'that opponent')
            question = f"Tell me about your game against {opponent}"
            context_type = "narrative"
        
        # Generate style-channeled response
        response = self.generate_style_channeled_response(question, context_type)
        
        # Ensure quality
        response = self.ensure_response_quality(response)
        
        # Check uniqueness
        response_hash = hashlib.md5(response.encode()).hexdigest()
        if response_hash in self.used_response_hashes:
            return self.generate_enhanced_training_example()
        
        self.used_response_hashes.add(response_hash)
        self.style_usage[context_type] += 1
        
        return {
            "messages": [
                {"role": "system", "content": self.create_style_based_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def ensure_response_quality(self, response: str) -> str:
        """Ensure response meets quality standards"""
        if len(response) < self.MIN_RESPONSE_LENGTH:
            # Add thoughtful elaboration that fits the player's style
            core_values = self.style_profile['core_values']
            if 'understanding' in core_values:
                response += " Understanding these principles deeply will transform your chess."
            elif 'beauty' in core_values:
                response += " This is where the true beauty of chess reveals itself."
            elif 'excellence' in core_values:
                response += " Striving for excellence in these details makes all the difference."
            else:
                response += " This approach will serve you well in your chess journey."
        
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
        
        return response.strip()
    
    def generate_style_focused_training_set(self, num_examples: int = 50) -> List[Dict]:
        """Generate training set focused on style demonstration"""
        print(f"\n🎭 Generating {num_examples} STYLE-FOCUSED examples for {self.player_name}")
        print("🚀 Using ChatGPT's 'channel the voice' approach...\n")
        
        examples = []
        
        for i in range(num_examples):
            try:
                example = self.generate_enhanced_training_example()
                examples.append(example)
                
                if (i + 1) % 10 == 0:
                    print(f"✅ Generated {i+1}/{num_examples} examples...")
                    
            except Exception as e:
                print(f"⚠️ Error generating example {i+1}: {str(e)}")
                continue
        
        # Report statistics
        self.report_style_statistics(examples)
        
        return examples
    
    def report_style_statistics(self, examples: List[Dict]):
        """Report statistics focused on style diversity"""
        print(f"\n📊 STYLE-FOCUSED Statistics for {self.player_name}:")
        print(f"   Total examples: {len(examples)}")
        print(f"   Unique responses: {len(self.used_response_hashes)}")
        
        # Quote usage (should be low!)
        total_quote_uses = sum(self.quote_usage.values())
        quote_percentage = (total_quote_uses / len(examples)) * 100 if examples else 0
        print(f"\n📚 Quote Usage Analysis:")
        print(f"   Direct quotes used: {quote_percentage:.1f}% of responses (Target: ~15%)")
        
        if total_quote_uses > 0:
            print(f"   Most used quotes:")
            for quote, count in self.quote_usage.most_common(3):
                print(f"     '{quote[:40]}...' : {count} times")
        
        # Style distribution
        print(f"\n🎭 Style Distribution:")
        total_styles = sum(self.style_usage.values())
        for style, count in self.style_usage.items():
            percentage = (count / total_styles) * 100 if total_styles > 0 else 0
            print(f"   {style}: {count} ({percentage:.1f}%)")
        
        # Quality metrics
        lengths = [len(ex['messages'][2]['content']) for ex in examples]
        avg_length = sum(lengths) / len(lengths) if lengths else 0
        print(f"\n📏 Response Quality:")
        print(f"   Average length: {avg_length:.0f} characters")
        print(f"   Target range: {self.MIN_RESPONSE_LENGTH}-{self.MAX_RESPONSE_LENGTH}")
        
        # Style focus score
        style_score = 100 - quote_percentage  # Higher is better
        print(f"\n🎯 Style Focus Score: {style_score:.1f}/100")
        if style_score > 80:
            print("   ✨ Excellent! Responses channel voice without over-relying on quotes")
        elif style_score > 60:
            print("   ✓ Good balance of style channeling and quote usage")
        else:
            print("   ⚠️ Consider reducing direct quote frequency")
    
    def save_style_focused_training_data(self, examples: List[Dict], filename: str = None):
        """Save style-focused training data"""
        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            safe_name = self.player_name.lower().replace(' ', '_')
            filename = f"{safe_name}_style_focused_{timestamp}.jsonl"
        
        # Save examples
        with open(filename, 'w', encoding='utf-8') as f:
            for example in examples:
                f.write(json.dumps(example, ensure_ascii=False) + '\n')
        
        print(f"\n💾 Saved {len(examples)} style-focused examples to {filename}")
        
        # Save metadata
        metadata = {
            'player_name': self.player_name,
            'approach': 'style_focused_channeling',
            'generation_date': datetime.now().isoformat(),
            'total_examples': len(examples),
            'quote_usage_rate': f"{(sum(self.quote_usage.values()) / len(examples) * 100):.1f}%" if examples else "0%",
            'style_profile': self.style_profile,
            'quote_usage_details': dict(self.quote_usage),
            'style_distribution': dict(self.style_usage)
        }
        
        metadata_filename = filename.replace('.jsonl', '_metadata.json')
        with open(metadata_filename, 'w', encoding='utf-8') as f:
            json.dump(metadata, f, indent=2)
        
        print(f"📋 Saved metadata to {metadata_filename}")
        
        # Show samples
        print("\n📝 Sample Style-Focused Examples:")
        for i in range(min(2, len(examples))):
            example = examples[i]
            print(f"\n--- Sample {i+1} ---")
            print(f"Q: {example['messages'][1]['content']}")
            print(f"A: {example['messages'][2]['content'][:200]}...")
            has_quote = any(quote in example['messages'][2]['content'] for quote in self.personality_data.get('authentic_quotes', []))
            print(f"Uses direct quote: {'Yes' if has_quote else 'No'}")
            print("-" * 50)
        
        return filename


def main():
    print("🎭 STYLE-FOCUSED CHESS PERSONALITY GENERATOR")
    print("=" * 55)
    print("Implements ChatGPT's 'channel the voice, don't just quote' approach")
    print("✨ Creates authentic personality through style demonstration\n")
    
    # Get player name
    player_name = input("Enter player name (e.g., 'Mikhail Tal'): ").strip()
    if not player_name:
        player_name = "Mikhail Tal"
    
    print(f"\n🎯 Generating STYLE-FOCUSED training data for {player_name}...")
    
    # Initialize style-focused generator
    generator = StyleFocusedPersonalityGenerator(player_name)
    
    if not generator.personality_data:
        print("\n❌ Cannot proceed without personality data!")
        return
    
    # Generate style-focused training data
    num_examples = int(input("\nHow many examples to generate? (default: 50): ") or "50")
    
    examples = generator.generate_style_focused_training_set(num_examples)
    
    # Save the style-focused data
    filename = generator.save_style_focused_training_data(examples)
    
    print(f"\n🎉 SUCCESS! STYLE-FOCUSED training data for {player_name} is ready!")
    print(f"\n🎭 Style-Focused Features:")
    print(f"   ✓ Channels voice without over-quoting (~15% quote usage)")
    print(f"   ✓ Demonstrates thinking patterns and decision styles")
    print(f"   ✓ Captures communication rhythm and emotional markers")
    print(f"   ✓ Embodies core values naturally in responses")
    print(f"   ✓ Creates authentic personality through reasoning style")
    print(f"   ✓ Universal approach works for any chess master")
    
    print(f"\n💫 This approach will create masters who:")
    print(f"   • Think like the real player, not just quote them")
    print(f"   • Show authentic personality through reasoning")
    print(f"   • Use quotes sparingly and contextually")
    print(f"   • Adapt their communication style to each situation")
    print(f"   • Feel genuinely conversational and natural")
    
    print(f"\n🚀 ChatGPT's advice has been brilliantly implemented!")


if __name__ == "__main__":
    main()