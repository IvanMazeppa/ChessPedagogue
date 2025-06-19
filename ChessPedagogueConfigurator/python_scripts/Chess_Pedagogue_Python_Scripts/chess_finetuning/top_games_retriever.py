import os
import json
import time
from typing import List, Dict
from openai import OpenAI

# Initialize OpenAI client
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

class UniversalChessGamesFetcher:
    def __init__(self, player_name: str):
        """Initialize with ANY player name!"""
        self.player_name = player_name
        print(f"🎯 Initializing fetcher for: {player_name}")
        
    def create_emoji_log(self, emoji: str, message: str):
        """Pretty logging with emojis"""
        print(f"{emoji} {message}", flush=True)
        
    def fetch_famous_games(self) -> List[Dict]:
        """Fetch famous games for the specified player"""
        self.create_emoji_log("🔍", f"Fetching famous {self.player_name} games...")
        
        # Using your working model
        model = "gpt-4.1-2025-04-14"  # Your sophisticated model!
        
        prompt = f"""Return a JSON object with exactly 10 of {self.player_name}'s most famous games.

CRITICAL: Return ONLY the JSON, no markdown formatting, no explanation.

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
            "significance": "Why this matters",
            "white_player": "Name",
            "black_player": "Name"
        }}
    ]
}}

Include games like:
- Tal vs Fischer (1959)
- Tal vs Botvinnik (1960)
- Tal's brilliant sacrifices
Keep moves under 100 moves unless absolutely necessary."""
        
        try:
            response = client.chat.completions.create(
                model=model,
                messages=[
                    {
                        "role": "system", 
                        "content": f"You are a chess historian specializing in {self.player_name}."
                    },
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                max_tokens=8000  # Removed the response_format that's causing issues
            )
            
            # Parse the response manually since json_object isn't supported
            content = response.choices[0].message.content
            
            # Clean up markdown formatting if present
            content = content.strip()
            if content.startswith('```json'):
                content = content[7:]  # Remove ```json
            if content.startswith('```'):
                content = content[3:]  # Remove ```
            if content.endswith('```'):
                content = content[:-3]  # Remove trailing ```
            
            # Try to extract JSON from the response
            import re
            
            # First try to parse the whole thing
            try:
                games_data = json.loads(content)
            except json.JSONDecodeError:
                # If that fails, try to find JSON in the content
                json_match = re.search(r'\{.*\}', content, re.DOTALL)
                if json_match:
                    games_data = json.loads(json_match.group())
                else:
                    # Last resort - try to find the games array
                    games_match = re.search(r'"games"\s*:\s*\[(.*?)\]', content, re.DOTALL)
                    if games_match:
                        games_data = {"games": json.loads('[' + games_match.group(1) + ']')}
                    else:
                        raise ValueError("Could not find valid JSON in response")
            self.create_emoji_log("✅", f"Retrieved data successfully!")
            
            # Save to file
            filename = f"{self.player_name.lower().replace(' ', '_')}_games.json"
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(games_data, f, indent=2, ensure_ascii=False)
            
            self.create_emoji_log("💾", f"Saved to {filename}")
            
            games = games_data.get("games", [])
            # Validate and clean games
            cleaned_games = []
            for game in games:
                # Skip games with obviously corrupted moves (like 400+ moves!)
                if len(game.get('moves', '').split('.')) > 150:
                    self.create_emoji_log("⚠️", f"Skipping corrupted game vs {game.get('opponent', 'Unknown')} - too many moves")
                    continue
                    
                # Check for required fields
                required = ['opponent', 'year', 'tournament', 'moves', 'result']
                if all(field in game for field in required):
                    cleaned_games.append(game)
                else:
                    self.create_emoji_log("⚠️", f"Skipping incomplete game vs {game.get('opponent', 'Unknown')}")
            
            self.create_emoji_log("📊", f"Found {len(cleaned_games)} valid games!")
            
            # Show preview
            if cleaned_games:
                print("\n🎮 Valid games found:")
                for i, game in enumerate(cleaned_games[:5]):  # Show first 5
                    moves_preview = game.get('moves', '')[:50] + '...' if len(game.get('moves', '')) > 50 else game.get('moves', '')
                    print(f"   {i+1}. vs {game.get('opponent', 'Unknown')} ({game.get('year', '?')})")
                    print(f"      Moves start: {moves_preview}")
            
            return cleaned_games
            
        except Exception as e:
            self.create_emoji_log("❌", f"Error: {str(e)}")
            
            # Check API key
            if "api_key" in str(e).lower():
                print("\n💡 API Key Troubleshooting:")
                print("1. Check if OPENAI_API_KEY is set:")
                print(f"   - Key exists: {'Yes' if os.getenv('OPENAI_API_KEY') else 'No'}")
                print(f"   - Key length: {len(os.getenv('OPENAI_API_KEY', ''))}")
                print("2. Make sure it starts with 'sk-'")
                print("3. Try: set OPENAI_API_KEY=your-key-here")
            
            return []


def fetch_games_for_player(player_name: str) -> List[Dict]:
    """Simple function to fetch games for any player"""
    fetcher = UniversalChessGamesFetcher(player_name)
    return fetcher.fetch_famous_games()


if __name__ == "__main__":
    print("🌟 Universal Chess Games Fetcher")
    print("================================\n")
    
    # CHANGE THIS TO ANY PLAYER!
    player_name = "Mikhail Tal"  # or "Bobby Fischer", "Garry Kasparov", etc.
    
    games = fetch_games_for_player(player_name)
    
    if games:
        print(f"\n✨ Success! Your {player_name} games are ready!")
    else:
        print("\n😔 No games found. Let's troubleshoot together!")