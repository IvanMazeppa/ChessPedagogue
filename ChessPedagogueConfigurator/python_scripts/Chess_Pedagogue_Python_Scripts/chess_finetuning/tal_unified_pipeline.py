#!/usr/bin/env python3
"""
Unified Tal Training Pipeline - Bringing It All Together!
Uses your enhanced personality data to create authentic training examples
Author: Ben
"""

import json
import random
import os
from datetime import datetime
from typing import List, Dict
from pathlib import Path

class UnifiedTalTrainingGenerator:
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
    
    def generate_tactical_response(self, question: str) -> str:
        """Generate response using ACTUAL personality data!"""
        
        # Determine what type of tactical question this is
        if "closed position" in question.lower() or "blocked" in question.lower():
            emotion = random.choice(self.personality['emotional_reactions']['facing_boring_position'])
            teaching = random.choice(self.personality['teaching_quirks']['opening_lines'])
            advice = "Look for pawn breaks! f5, g5, h5 - these aren't just moves, they're explosions waiting to happen!"
            
        elif "sacrifice" in question.lower():
            emotion = random.choice(self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice'])
            teaching = self.personality['teaching_quirks']['encouragement_style'][2]
            advice = random.choice(self.personality['authentic_quotes'])
            
        elif "attack" in question.lower():
            emotion = "*eyes gleam with excitement*"
            teaching = self.personality['teaching_quirks']['opening_lines'][1]
            advice = "Every piece should aim at the enemy king. Even the pawns can be battering rams!"
            
        else:
            # General tactical advice
            emotion = "*grins mischievously*"
            teaching = random.choice(self.personality['teaching_quirks']['opening_lines'])
            advice = random.choice(self.personality['teaching_quirks']['unique_approaches'])
        
        # Add a personal story or rival comparison
        story_options = [
            f"Petrosian once told me I attack too much. I said: '{self.personality['rival_opinions']['Petrosian']}'",
            f"In my game against {random.choice([g['opponent'] for g in self.games[:3]])} I proved that tactics flow from superior activity!",
            f"Remember: {random.choice(self.personality['authentic_quotes'])}",
            f"{random.choice(self.personality['personal_quirks'])}"
        ]
        
        story = random.choice(story_options)
        
        # Build response
        response = f"""{emotion} {teaching}

{advice}

{story}

{random.choice(self.personality['teaching_quirks']['encouragement_style'])}"""
        
        return response
    
    def generate_rival_discussion(self) -> Dict:
        """Create a rival discussion using real opinions"""
        rival = random.choice(list(self.personality['rival_opinions'].keys()))
        opinion = self.personality['rival_opinions'][rival]
        
        question = f"What did you think of {rival}?"
        
        response = f"""*{random.choice(['laughs warmly', 'eyes twinkle', 'grins'])}* 

{opinion}

But you know what? Chess needed {rival}! Without defenders like him, attackers like me would have no challenge. We pushed each other to new heights.

{random.choice(self.personality['humor_style']['examples'])}

Every game against {rival} taught me something new - usually about patience, which I never quite mastered! *winks*"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def generate_game_story(self) -> Dict:
        """Create a game story using actual games"""
        if not self.games:
            return self.generate_rival_discussion()  # Fallback
            
        game = random.choice(self.games)
        question = f"Tell me about your game against {game['opponent']} in {game['year']}!"
        
        # Use emotional reactions
        emotion = "*eyes light up with memory*"
        reaction = random.choice(self.personality['emotional_reactions']['seeing_a_brilliant_sacrifice'])
        
        response = f"""{emotion} {game['opponent']} in {game['year']}! {reaction}

This was during the {game['tournament']}, and {game['significance']}

After {game['opening']}, the position was exactly my type - full of hidden tactics! The key moment came when I sacrificed... well, let's just say {game['opponent']} wasn't expecting it!

{random.choice(self.personality['humor_style']['examples'])}

{random.choice(self.personality['winning_phrases'] if game['result'] == '1-0' else self.personality['losing_phrases'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def generate_philosophical_response(self) -> Dict:
        """Create philosophical responses using personality data"""
        questions = [
            "What does chess mean to you?",
            "How do you handle losses?",
            "Why take risks in chess?"
        ]
        
        question = random.choice(questions)
        
        if "loss" in question.lower():
            phrases = self.personality['losing_phrases']
        else:
            phrases = self.personality['authentic_quotes']
            
        response = f"""*contemplates deeply*

{random.choice(phrases)}

You see, chess taught me that beauty matters more than perfection. {random.choice(self.personality['teaching_quirks']['unique_approaches'])}

{random.choice(self.personality['humor_style']['examples'])}

In the end, we're all just trying to create something memorable on 64 squares. Whether we win or lose, the beauty remains!"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": question},
                {"role": "assistant", "content": response}
            ]
        }
    
    def generate_teaching_moment(self) -> Dict:
        """Create teaching examples using actual teaching style"""
        scenarios = [
            ("I played too safely and got a worse position", "facing_boring_position"),
            ("I missed a tactical shot", "seeing_a_brilliant_sacrifice"),
            ("I'm afraid to sacrifice material", "seeing_a_brilliant_sacrifice")
        ]
        
        student_comment, reaction_type = random.choice(scenarios)
        
        emotion = random.choice(['*eyes sparkle*', '*leans forward excitedly*', '*grins*'])
        reaction = random.choice(self.personality['emotional_reactions'][reaction_type])
        encouragement = random.choice(self.personality['teaching_quirks']['encouragement_style'])
        teaching = random.choice(self.personality['teaching_quirks']['opening_lines'])
        
        response = f"""{emotion} {reaction}

{teaching}

{encouragement}

Here's what I learned from {random.choice(list(self.personality['rival_opinions'].keys()))}: even the best defenders fear the attacker who isn't afraid to lose!

{random.choice(self.personality['personal_quirks'])}"""
        
        return {
            "messages": [
                {"role": "system", "content": self.create_system_prompt()},
                {"role": "user", "content": student_comment},
                {"role": "assistant", "content": response}
            ]
        }
    
    def generate_training_data(self, num_examples: int = 50) -> List[Dict]:
        """Generate diverse training examples using ALL your data!"""
        print(f"\n🎨 Generating {num_examples} authentic Tal examples...")
        
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
            "What makes a position tactical?"
        ]
        
        examples = []
        
        for i in range(num_examples):
            try:
                if i % 5 < 2:  # 40% tactical
                    question = tactical_questions[i % len(tactical_questions)]
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
                
                if i % 10 == 0:
                    print(f"   Generated {i+1}/{num_examples} examples...")
                    
            except Exception as e:
                print(f"⚠️ Error on example {i}: {str(e)}")
                continue
        
        print(f"✅ Generated {len(examples)} authentic examples!")
        return examples
    
    def save_training_data(self, examples: List[Dict], filename: str = None):
        """Save the training data"""
        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"tal_unified_training_{timestamp}.jsonl"
        
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
            print(f"A: {example['messages'][2]['content'][:200]}...")
        
        return filename


def main():
    print("✨ UNIFIED TAL TRAINING PIPELINE")
    print("=" * 40)
    print("Bringing together personality + games + teaching style!\n")
    
    generator = UnifiedTalTrainingGenerator()
    
    if not generator.personality:
        print("\n❌ Cannot proceed without personality data!")
        print("Make sure 'mikhail_tal_enhanced.json' exists")
        return
    
    # Generate training data
    examples = generator.generate_training_data(100)  # or whatever number you want
    
    # Save it
    filename = generator.save_training_data(examples)
    
    print(f"\n🎉 SUCCESS! Your unified training data is ready!")
    print(f"✨ This data captures Tal's authentic voice using:")
    print(f"   - Real quotes and opinions")
    print(f"   - Actual games and stories")
    print(f"   - True personality quirks")
    print(f"   - Genuine teaching style")
    
    print(f"\n🚀 Ready for fine-tuning with: {filename}")


if __name__ == "__main__":
    main()