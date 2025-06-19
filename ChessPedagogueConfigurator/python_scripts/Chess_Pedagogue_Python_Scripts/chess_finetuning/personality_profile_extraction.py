import os
import re
import json
from typing import List, Dict, Set, Tuple
from openai import OpenAI
import requests
from bs4 import BeautifulSoup

class AutomatedPersonalityExtractor:
    def __init__(self):
        self.client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
        
        # Known sources for authentic quotes
        self.quote_sources = {
            "Mikhail Tal": {
                "books": ["The Life and Games of Mikhail Tal"],
                "known_quotes": [
                    "You must take your opponent into a deep dark forest where 2+2=5, and the path leading out is only wide enough for one.",
                    "There are two types of sacrifices: correct ones and mine.",
                    "I drink, I smoke, I gamble, I chase women – but postal chess is one vice I don't have.",
                    "If you wait for luck to turn up, life becomes very boring."
                ],
                "quote_patterns": [
                    r'"([^"]+)".*?(?:Tal|said|wrote)',  # Direct quotes
                    r'Tal (?:said|wrote|remarked|observed|noted)[:\s]+["\']([^"\']+)["\']',
                    r'(?:famous|memorable) (?:quote|saying|words).*?["\']([^"\']+)["\']'
                ]
            },
            "Bobby Fischer": {
                "books": ["My 60 Memorable Games", "Bobby Fischer Teaches Chess"],
                "known_quotes": [
                    "I don't believe in psychology. I believe in good moves.",
                    "You can only get good at chess if you love the game.",
                    "Tactics flow from a superior position.",
                    "Best by test.",
                    "Chess is war over the board. The object is to crush the opponent's mind."
                ],
                "quote_patterns": [
                    r'"([^"]+)".*?(?:Fischer|Bobby)',
                    r'Fischer (?:said|declared|insisted)[:\s]+["\']([^"\']+)["\']'
                ]
            },
            "Alexander Alekhine": {
                "books": ["My Best Games of Chess", "Alexander Alekhine's Best Games"],
                "known_quotes": [
                    "I think that chess is a science in the form of a game.",
                    "Chess for me is not a game, but an art.",
                    "To win against me, you must beat me three times: in the opening, the middlegame, and the endgame.",
                    "During a chess competition a chess master should be a combination of a beast of prey and a monk."
                ],
                "quote_patterns": [
                    r'"([^"]+)".*?(?:Alekhine)',
                    r'Alekhine.*?(?:wrote|noted)[:\s]+["\']([^"\']+)["\']'
                ]
            }
        }
    
    def extract_quotes_from_text(self, text: str, player_name: str) -> List[str]:
        """Extract quotes using regex patterns specific to the player"""
        quotes = []
        patterns = self.quote_sources.get(player_name, {}).get("quote_patterns", [])
        
        for pattern in patterns:
            matches = re.findall(pattern, text, re.IGNORECASE | re.MULTILINE)
            quotes.extend(matches)
        
        # Also look for game annotations (often contain personality)
        annotation_pattern = r'(?:(?:\d+\.\.\.|\d+\.)\s*[a-zA-Z][a-h1-8+#!?]+)\s*([A-Z][^.!?]*[.!?])'
        annotation_matches = re.findall(annotation_pattern, text)
        
        # Filter for sentences that show personality
        personality_indicators = ['!', 'beautiful', 'must', 'forced', 'only', 'brilliant', 
                                'sacrifice', 'attack', 'crush', 'destroy', 'magic']
        
        for match in annotation_matches:
            if any(indicator in match.lower() for indicator in personality_indicators):
                quotes.append(match)
        
        # Clean and deduplicate
        cleaned_quotes = []
        for quote in quotes:
            cleaned = quote.strip()
            if len(cleaned) > 20 and cleaned not in cleaned_quotes:
                cleaned_quotes.append(cleaned)
        
        return cleaned_quotes[:20]  # Top 20 most relevant
    
    def search_famous_quotes(self, player_name: str) -> List[str]:
        """Search for famous quotes using multiple methods"""
        all_quotes = []
        
        # Method 1: Use known quotes database
        known = self.quote_sources.get(player_name, {}).get("known_quotes", [])
        all_quotes.extend(known)
        
        # Method 2: Use GPT to recall famous quotes
        try:
            prompt = f"""List 10 AUTHENTIC, VERIFIED quotes from {player_name}.
            These must be real quotes, not paraphrases or made-up quotes.
            Focus on quotes that reveal personality, not just chess wisdom.
            Include only quotes that can be found in books or reliable sources.
            
            Format: One quote per line, no attribution needed."""
            
            response = self.client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "You are a chess historian who only provides verified, authentic quotes."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3  # Low temperature for accuracy
            )
            
            quotes = response.choices[0].message.content.strip().split('\n')
            all_quotes.extend([q.strip(' "\'') for q in quotes if q.strip()])
            
        except Exception as e:
            print(f"Error fetching quotes: {e}")
        
        return list(set(all_quotes))  # Remove duplicates
    
    def analyze_quote_patterns(self, quotes: List[str]) -> Dict[str, any]:
        """Analyze quotes to extract speech patterns and personality"""
        
        analysis = {
            "exclamation_frequency": sum(1 for q in quotes if '!' in q) / max(len(quotes), 1),
            "question_frequency": sum(1 for q in quotes if '?' in q) / max(len(quotes), 1),
            "average_length": sum(len(q) for q in quotes) / max(len(quotes), 1),
            "common_starters": [],
            "emotional_words": [],
            "unique_phrases": [],
            "speaking_style": ""
        }
        
        # Extract sentence starters
        starters = []
        for quote in quotes:
            words = quote.split()
            if words:
                starter = ' '.join(words[:3])
                starters.append(starter)
        
        # Find most common starters (but exclude generic ones)
        generic_starters = ["I think", "The game", "In this", "It is", "You must", "Ah,"]
        analysis["common_starters"] = [s for s in set(starters) 
                                      if not any(g in s for g in generic_starters)][:5]
        
        # Extract emotional/personality words
        emotional_indicators = {
            "passionate": ["must", "destroy", "crush", "brilliant", "explosion", "fire"],
            "humorous": ["funny", "laugh", "amusing", "joke", "smile"],
            "precise": ["exactly", "precisely", "clearly", "obviously", "forced"],
            "poetic": ["beautiful", "dance", "sing", "poetry", "art", "soul"],
            "aggressive": ["attack", "crush", "destroy", "kill", "sacrifice"]
        }
        
        found_emotions = set()
        for quote in quotes:
            lower_quote = quote.lower()
            for emotion, words in emotional_indicators.items():
                if any(word in lower_quote for word in words):
                    found_emotions.add(emotion)
        
        analysis["speaking_style"] = ', '.join(found_emotions) if found_emotions else "analytical"
        
        return analysis
    
    def create_personality_from_quotes(self, player_name: str, quotes: List[str]) -> Dict[str, any]:
        """Create a complete personality profile from quotes"""
        
        analysis = self.analyze_quote_patterns(quotes)
        
        # Build personality profile
        personality = {
            "name": player_name,
            "authentic_quotes": quotes[:10],  # Top 10 for training
            "speech_patterns": self.extract_speech_patterns(quotes),
            "personality_traits": analysis["speaking_style"].split(', '),
            "exclamation_usage": "high" if analysis["exclamation_frequency"] > 0.3 else "moderate",
            "sentence_starters": analysis["common_starters"],
            "forbidden_phrases": ["Ah!", "Let me show you", "As you can see", "Obviously"],
            "writing_samples": self.generate_writing_samples(player_name, quotes, analysis)
        }
        
        return personality
    
    def extract_speech_patterns(self, quotes: List[str]) -> List[str]:
        """Extract recurring speech patterns"""
        patterns = []
        
        # Look for repeated structures
        for i, quote in enumerate(quotes):
            # Check for "X is Y" patterns
            if " is " in quote:
                structure = re.sub(r'\b\w+\b', 'X', quote, count=2)
                if structure not in patterns:
                    patterns.append(f"Structure: {structure}")
        
        # Look for characteristic phrases
        two_word_phrases = {}
        for quote in quotes:
            words = quote.lower().split()
            for i in range(len(words) - 1):
                phrase = f"{words[i]} {words[i+1]}"
                two_word_phrases[phrase] = two_word_phrases.get(phrase, 0) + 1
        
        # Get repeated phrases (appearing 2+ times)
        repeated = [phrase for phrase, count in two_word_phrases.items() if count >= 2]
        patterns.extend([f"Often says: '{phrase}'" for phrase in repeated[:5]])
        
        return patterns
    
    def generate_writing_samples(self, player_name: str, quotes: List[str], analysis: Dict) -> List[str]:
        """Generate sample writings in the player's style"""
        
        prompt = f"""Based on these authentic quotes from {player_name}:
{chr(10).join(quotes[:5])}

Generate 3 short examples of how {player_name} would START explaining a chess position.
Rules:
- Use their actual speech patterns from the quotes
- Match their emotional tone ({analysis['speaking_style']})
- Do NOT use generic phrases like "Ah!" or "Let me show you"
- Each example should be different
- 1-2 sentences each

Format: One example per line."""

        try:
            response = self.client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": f"You are {player_name}. Write exactly as they would based on their quotes."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.7
            )
            
            samples = response.choices[0].message.content.strip().split('\n')
            return [s.strip() for s in samples if s.strip()][:3]
            
        except:
            return []
    
    def extract_from_pgn_annotations(self, pgn_text: str) -> List[str]:
        """Extract personality from chess game annotations"""
        personality_snippets = []
        
        # Pattern for annotations (text in {} or after moves)
        annotation_pattern = r'\{([^}]+)\}|(?:\d+\.(?:\.\.)?)\s*[a-zA-Z][a-h1-8+#!?]+\s*([A-Z][^{]*?)(?=\d+\.|$)'
        
        matches = re.findall(annotation_pattern, pgn_text, re.MULTILINE | re.DOTALL)
        
        for match in matches:
            text = match[0] or match[1]
            if text and len(text) > 30:  # Meaningful annotations
                personality_snippets.append(text.strip())
        
        return personality_snippets
    
    def build_complete_personality(self, player_name: str, book_text: str = None) -> Dict[str, any]:
        """Main method: Build personality from all available sources"""
        
        print(f"🔍 Building personality for {player_name}...")
        
        all_quotes = []
        
        # 1. Get known famous quotes
        famous_quotes = self.search_famous_quotes(player_name)
        all_quotes.extend(famous_quotes)
        print(f"  ✓ Found {len(famous_quotes)} famous quotes")
        
        # 2. Extract from book text if provided
        if book_text:
            extracted = self.extract_quotes_from_text(book_text, player_name)
            all_quotes.extend(extracted)
            print(f"  ✓ Extracted {len(extracted)} quotes from text")
        
        # 3. Remove duplicates and clean
        unique_quotes = []
        seen = set()
        for quote in all_quotes:
            cleaned = quote.strip(' "\'.,!?').lower()
            if cleaned not in seen and len(cleaned) > 20:
                seen.add(cleaned)
                unique_quotes.append(quote)
        
        print(f"  ✓ Total unique quotes: {len(unique_quotes)}")
        
        # 4. Create personality profile
        personality = self.create_personality_from_quotes(player_name, unique_quotes)
        
        # 5. Save for future use
        filename = f"{player_name.lower().replace(' ', '_')}_personality.json"
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(personality, f, indent=2, ensure_ascii=False)
        
        print(f"  ✓ Saved personality to {filename}")
        
        return personality


# Example usage
if __name__ == "__main__":
    extractor = AutomatedPersonalityExtractor()
    
    # Method 1: Just from famous quotes (simplest)
    tal_personality = extractor.build_complete_personality("Mikhail Tal")
    
    print("\n🎭 Tal's Personality Profile:")
    print(f"Authentic quotes: {len(tal_personality['authentic_quotes'])}")
    print(f"First quote: {tal_personality['authentic_quotes'][0]}")
    print(f"Personality: {', '.join(tal_personality['personality_traits'])}")
    print(f"Sample opening: {tal_personality['writing_samples'][0] if tal_personality['writing_samples'] else 'N/A'}")