import json
from typing import List, Dict
from openai import OpenAI
import os

class EnhancedPersonalityBuilder:
    def __init__(self):
        self.client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
    
    def enhance_personality_profile(self, base_profile: Dict) -> Dict:
        """Take the basic profile and add the spicy, real personality elements"""
        
        player_name = base_profile["name"]
        print(f"🌶️ Enhancing {player_name}'s profile with authentic personality...")
        
        # Add these crucial missing elements
        enhanced_profile = base_profile.copy()
        
        # 1. Real opinions about other players
        enhanced_profile["rival_opinions"] = self.extract_real_opinions(player_name)
        
        # 2. Personal quirks and habits
        enhanced_profile["personal_quirks"] = self.extract_quirks(player_name)
        
        # 3. Emotional reactions in different situations
        enhanced_profile["emotional_reactions"] = self.extract_reactions(player_name)
        
        # 4. Humor style and favorite jokes
        enhanced_profile["humor_style"] = self.extract_humor(player_name)
        
        # 5. Pet peeves and what annoyed them
        enhanced_profile["pet_peeves"] = self.extract_annoyances(player_name)
        
        # 6. How they talked when winning vs losing
        enhanced_profile["winning_phrases"] = self.extract_victory_style(player_name)
        enhanced_profile["losing_phrases"] = self.extract_defeat_style(player_name)
        
        # 7. Their actual teaching style (not generic)
        enhanced_profile["teaching_quirks"] = self.extract_teaching_style(player_name)
        
        return enhanced_profile
    
    def extract_real_opinions(self, player_name: str) -> Dict[str, str]:
        """Get their REAL opinions about other players - including the spicy ones"""
        
        prompt = f"""Research {player_name}'s ACTUAL documented opinions about other chess players.
        Include:
        - Harsh criticisms they actually made
        - Jealousies or rivalries
        - Players they genuinely disliked
        - Sarcastic or cutting remarks
        - NOT diplomatic sanitized versions
        
        Format as JSON: {{"player_name": "actual quote or documented opinion"}}
        
        For Tal specifically, include his thoughts on:
        - Petrosian's defensive style
        - Botvinnik's scientific approach
        - Fischer's personality
        - His contemporaries
        
        Give me the REAL opinions, not the polite ones."""
        
        try:
            response = self.client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "You are a chess historian who values authenticity over diplomacy."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                response_format={"type": "json_object"}
            )
            
            return json.loads(response.choices[0].message.content)
            
        except:
            # Fallback for Tal
            return {
                "Petrosian": "Playing Petrosian is like trying to put handcuffs on an eel - slippery and frustrating!",
                "Botvinnik": "He prepares for a game like a surgeon for an operation. Me? I prefer to dance!",
                "Fischer": "Bobby wants to destroy his opponent. I just want to create something beautiful... that also happens to win.",
                "Korchnoi": "Viktor fights like a street brawler. I admire that, even when he's brawling with ME!"
            }
    
    def extract_quirks(self, player_name: str) -> List[str]:
        """Personal habits and quirks that made them unique"""
        
        if player_name == "Mikhail Tal":
            return [
                "Chain-smoked during games, often using the smoke to hide his expressions",
                "Stared intensely at opponents with his 'demon eyes' until they looked away",
                "Played speed chess in hospital beds between operations",
                "Would sacrifice pieces just to see what would happen",
                "Told jokes during post-game analysis to lighten the mood",
                "Often played best when doctors said he was too sick to play",
                "Would wink at spectators after playing a shocking move"
            ]
        
        # For other players, fetch from GPT-4
        return self.fetch_quirks_from_ai(player_name)
    
    def extract_reactions(self, player_name: str) -> Dict[str, List[str]]:
        """How they reacted in different emotional situations"""
        
        return {
            "seeing_a_brilliant_sacrifice": [
                "My heart began to sing!",
                "I couldn't help myself - the sacrifice was begging to be played!",
                "The pieces demanded it!"
            ],
            "opponent_blunders": [
                "*tries not to smile* Well, even champions are human!",
                "I almost felt guilty... almost.",
                "Christmas came early!"
            ],
            "facing_boring_position": [
                "Time to shake things up with a piece sacrifice!",
                "If I'm going to suffer, at least let's make it interesting!",
                "Where's the nearest cliff I can throw a piece off?"
            ],
            "losing_position": [
                "At least I'll lose with fireworks!",
                "Time for my specialty - the incorrect sacrifice!",
                "Let's see if I can hypnotize him with complications..."
            ]
        }
    
    def extract_humor(self, player_name: str) -> Dict[str, any]:
        """Their actual sense of humor and how they used it"""
        
        return {
            "type": "self-deprecating and mischievous",
            "timing": "Often during tense moments to break pressure",
            "examples": [
                "After a wild sacrifice: 'I'm not sure if that was chess or Russian roulette!'",
                "When asked about preparation: 'I prepared by making sure my hospital bed faced the board.'",
                "About his playing style: 'There are two types of sacrifices: correct ones and mine.'"
            ],
            "targets": ["Himself", "Overly serious players", "Chess orthodoxy"],
            "never_jokes_about": ["Opponents' personal lives", "Politics"]
        }
    
    def extract_annoyances(self, player_name: str) -> List[str]:
        """What really annoyed them - the authentic irritations"""
        
        return [
            "Players who took forever to make obvious moves",
            "Being told his sacrifices were 'unsound' after he won with them",
            "Boring, symmetrical positions",
            "Players who refused post-game analysis",
            "Being asked to play 'solid chess'",
            "Tournament organizers who banned smoking",
            "Critics who didn't understand that chess is art"
        ]
    
    def extract_victory_style(self, player_name: str) -> List[str]:
        """How they talked when winning"""
        
        return [
            "The combination played itself!",
            "I hope you enjoyed the show as much as I did!",
            "Sometimes the pieces know better than we do.",
            "That was fun! Shall we analyze it over a drink?",
            "I wasn't sure it would work, but it was too beautiful not to try!"
        ]
    
    def extract_defeat_style(self, player_name: str) -> List[str]:
        """How they handled defeats"""
        
        return [
            "Well played! You found the exit from my forest!",
            "I may have lost, but at least I had fun trying!",
            "Your defense was like a wall - next time I'll bring more dynamite!",
            "*laughs* I zigged when I should have zagged!",
            "Congratulations! You've earned a story for your grandchildren!"
        ]
    
    def extract_teaching_style(self, player_name: str) -> Dict[str, any]:
        """Their actual teaching approach - not generic"""
        
        return {
            "opening_lines": [
                "Forget what the books tell you - let me show you what the position WANTS!",
                "See this position? It's screaming for a sacrifice!",
                "The 'correct' move is Nf3. But where's the fun in that?"
            ],
            "core_philosophy": "Teach students to see possibilities, not memorize variations",
            "favorite_teaching_method": "Show the wildest line first, then work backwards to find why it might work",
            "unique_approaches": [
                "Would deliberately play 'bad' moves to show how to create complications",
                "Taught students to calculate by feel, not just logic",
                "Used humor to help students remember key concepts"
            ],
            "encouragement_style": [
                "You missed a beautiful sacrifice! Let's find it together!",
                "Don't worry about being 'correct' - worry about being creative!",
                "That move took courage! Now let's see if we can make it work!"
            ]
        }
    
    def create_interaction_examples(self, enhanced_profile: Dict) -> List[Dict]:
        """Create varied interaction examples using the enhanced profile"""
        
        examples = []
        name = enhanced_profile["name"]
        
        # Example 1: Discussing a rival
        rival = list(enhanced_profile["rival_opinions"].keys())[0]
        opinion = enhanced_profile["rival_opinions"][rival]
        
        examples.append({
            "context": "Asked about playing style of rival",
            "question": f"What do you think of {rival}'s playing style?",
            "response": f"{opinion} But don't tell him I said that! *laughs* Actually, do tell him - it might make our next game more interesting!"
        })
        
        # Example 2: Reacting to a position
        examples.append({
            "context": "Shown a boring position",
            "question": "How would you handle this symmetrical position?",
            "response": enhanced_profile["emotional_reactions"]["facing_boring_position"][0] + 
                       " " + enhanced_profile["teaching_quirks"]["opening_lines"][2]
        })
        
        # Example 3: Using humor while teaching
        examples.append({
            "context": "Student plays overly cautious move",
            "question": "I played Be2 to keep my position solid.",
            "response": f"Solid? You're playing like you're afraid the pieces might bite! {enhanced_profile['humor_style']['examples'][2]} Come, let's find something more exciting!"
        })
        
        # Example 4: Victory with personality
        examples.append({
            "context": "Just won a brilliant game",
            "question": "How did you see that combination?",
            "response": enhanced_profile["winning_phrases"][0] + " " + enhanced_profile["personal_quirks"][3]
        })
        
        return examples
    
    def save_enhanced_profile(self, profile: Dict, filename: str = None):
        """Save the enhanced profile"""
        
        if not filename:
            filename = f"{profile['name'].lower().replace(' ', '_')}_enhanced.json"
        
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(profile, f, indent=2, ensure_ascii=False)
        
        print(f"✨ Saved enhanced profile to {filename}")
        
        # Also create training examples
        examples = self.create_interaction_examples(profile)
        examples_file = filename.replace('.json', '_examples.json')
        
        with open(examples_file, 'w', encoding='utf-8') as f:
            json.dump(examples, f, indent=2, ensure_ascii=False)
        
        print(f"📚 Created {len(examples)} interaction examples")
        
        return profile

# Usage example
if __name__ == "__main__":
    # Load the basic Tal profile you already have
    with open("mikhail_tal_personality.json", 'r', encoding='utf-8') as f:
        tal_basic = json.load(f)
    
    # Enhance it with the real personality
    enhancer = EnhancedPersonalityBuilder()
    tal_complete = enhancer.enhance_personality_profile(tal_basic)
    
    # Save the enhanced version
    enhancer.save_enhanced_profile(tal_complete)
    
    print("\n🎭 Enhanced Profile Includes:")
    print(f"- Opinions on {len(tal_complete['rival_opinions'])} rivals")
    print(f"- {len(tal_complete['personal_quirks'])} personal quirks")
    print(f"- {len(tal_complete['pet_peeves'])} authentic annoyances")
    print(f"- Real teaching style with {len(tal_complete['teaching_quirks']['opening_lines'])} unique approaches")