import json
import requests
from bs4 import BeautifulSoup
import openai
import re
import time
import os
import traceback

# Set your OpenAI API key
openai.api_key = os.environ.get("OPENAI_API_KEY")

def create_chess_master_training_data(master_name, num_examples=10):
    """
    Create training data for a chess grandmaster automatically
    """
    print(f"✨ Creating training data for {master_name}...")
    
    try:
        # Step 1: Research the grandmaster
        master_info = research_grandmaster(master_name)
        
        # Step 2: Generate training examples using the research
        examples = []
        
        # Add system message that captures the master's essence
        style_desc = str(master_info.get('style_description', 'distinctive'))
        teaching_approach = str(master_info.get('teaching_approach', 'You explain chess concepts clearly.'))
        system_message = f"You are Coach {master_name}, a chess teacher modeling the {style_desc} style of {master_name}. {teaching_approach}"
        
        # Create examples for different question types
        try:
            examples.append(create_game_analysis_example(master_name, system_message, master_info))
        except Exception as e:
            print(f"⚠️ Error creating game analysis example: {e}")
            # Add a fallback example
            examples.append(create_fallback_example(master_name, system_message, "game analysis"))
            
        try:
            examples.append(create_opening_philosophy_example(master_name, system_message, master_info))
        except Exception as e:
            print(f"⚠️ Error creating opening philosophy example: {e}")
            examples.append(create_fallback_example(master_name, system_message, "opening philosophy"))
            
        try:
            examples.append(create_middlegame_advice_example(master_name, system_message, master_info))
        except Exception as e:
            print(f"⚠️ Error creating middlegame advice example: {e}")
            examples.append(create_fallback_example(master_name, system_message, "middlegame advice"))
            
        try:
            examples.append(create_endgame_technique_example(master_name, system_message, master_info))
        except Exception as e:
            print(f"⚠️ Error creating endgame technique example: {e}")
            examples.append(create_fallback_example(master_name, system_message, "endgame technique"))
            
        try:
            examples.append(create_chess_history_example(master_name, system_message, master_info))
        except Exception as e:
            print(f"⚠️ Error creating chess history example: {e}")
            examples.append(create_fallback_example(master_name, system_message, "chess history"))
        
        # Add famous games examples safely
        famous_games = master_info.get('famous_games', [])
        if isinstance(famous_games, list):
            for i, game in enumerate(famous_games[:10]):  # Use up to 10 famous games
                try:
                    examples.append(create_famous_game_example(master_name, system_message, game))
                except Exception as e:
                    print(f"⚠️ Error creating famous game example {i+1}: {e}")
                    examples.append(create_fallback_example(master_name, system_message, "famous game"))
        
        # Add general teaching examples to reach the target number
        for i in range(max(0, num_examples - len(examples))):
            try:
                examples.append(create_general_teaching_example(master_name, system_message, master_info, i))
            except Exception as e:
                print(f"⚠️ Error creating general teaching example {i+1}: {e}")
                examples.append(create_fallback_example(master_name, system_message, "chess teaching"))
        
        # Step 3: Save the examples to a JSONL file
        output_file = f"{master_name.lower().replace(' ', '_')}_training.jsonl"
        with open(output_file, 'w') as f:
            for example in examples:
                f.write(json.dumps(example) + '\n')
        
        print(f"✅ Created {len(examples)} training examples for {master_name}!")
        print(f"📄 Saved to {output_file}")
        
        return examples
        
    except Exception as e:
        print(f"❌ Error creating training data for {master_name}: {e}")
        traceback.print_exc()
        return []

def create_fallback_example(name, system_message, example_type):
    """Create a fallback example when other methods fail"""
    questions = {
        "game analysis": "What do you think of this standard opening position?",
        "opening philosophy": "What was your approach to chess openings?",
        "middlegame advice": "How should I approach complex middlegame positions?",
        "endgame technique": "What's your advice for improving endgame play?",
        "chess history": "Tell me about your chess career and achievements.",
        "famous game": "Can you tell me about one of your most famous games?",
        "chess teaching": "What's your most important advice for chess improvement?"
    }
    
    responses = {
        "game analysis": f"This is a common opening position that requires careful development. In my approach to such positions, I focus on controlling the center while developing pieces harmoniously. The key principle here is to establish a strong pawn structure and activate your pieces toward the center. Always consider your opponent's potential responses and have a clear plan for the middlegame that will emerge.",
        "opening philosophy": f"In my approach to openings, I favored positions that aligned with my strengths as a player. I believed in learning the principles behind openings rather than memorizing variations. The purpose of the opening is to reach a middlegame position where you can apply your strategic understanding. I always encouraged players to develop a cohesive opening repertoire that suits their playing style and temperament.",
        "middlegame advice": f"In complex middlegame positions, I always focused on key strategic elements. Start by evaluating the pawn structure to understand the character of the position. Look for weaknesses you can target and strengths you can build upon. Piece coordination is essential - make sure your pieces work together toward common goals. When calculating variations, consider the long-term implications of each move. The middlegame is where chess personality truly emerges.",
        "endgame technique": f"Endgame success comes from mastering fundamental positions and principles. First, study the essential theoretical endgames: king and pawn versus king, rook endgames, and opposite-colored bishop endings. Always activate your king immediately in the endgame - it transforms from a piece needing protection to a powerful attacker. Practice precise calculation and develop patience. In endgames, technique and understanding outweigh intuition.",
        "chess history": f"My chess career was defined by my distinctive style and approach to the game. What set me apart from other players of my era was my approach to chess - I combined deep strategic understanding with tactical precision. Throughout my career, I faced many legendary opponents, each presenting unique challenges that helped shape my evolution as a player. The chess world was quite different in my era compared to today's computer-dominated environment.",
        "famous game": f"One of my most memorable games demonstrated the essence of my chess style. The critical moment came when I found a move that surprised many observers and showcased my characteristic approach to the position. This game displayed my chess philosophy: focusing on piece activity, dynamic play, and creating practical problems for my opponent. When studying this game, pay attention to how the strategic elements and tactical opportunities intertwined.",
        "chess teaching": f"The key to chess improvement lies in understanding the game's fundamental principles, not just memorizing variations. In my experience, players grow most when they study both strategy and tactics in balance. Analyze your games honestly, learn from your mistakes, and focus on the areas where you struggle most. Chess is a journey of continuous learning, and even at the highest levels, there's always room for growth."
    }
    
    return {
        "messages": [
            {"role": "system", "content": system_message},
            {"role": "user", "content": questions.get(example_type, "What advice would you give to chess players?")},
            {"role": "assistant", "content": responses.get(example_type, f"As {name}, I would advise focusing on understanding chess principles rather than memorizing variations. Study the classics, analyze your games, and develop a style that suits your personality.")}
        ]
    }

def research_grandmaster(name):
    """Research a chess grandmaster using Wikipedia and chess databases"""
    print(f"🔍 Researching {name}...")
    
    # Initialize an info dictionary with defaults
    master_info = {
        'name': name,
        'birth_year': '',
        'death_year': '',
        'nationality': '',
        'world_champion': False,
        'world_champion_years': '',
        'style_keywords': ['tactical', 'strategic', 'positional'],
        'style_description': 'distinctive',
        'teaching_approach': 'You explain chess concepts clearly and provide concrete examples.',
        'famous_games': [
            {'opponent': 'a strong grandmaster', 'year': '19XX', 'tournament': 'a major event', 
             'opening': 'a major opening', 'why_famous': 'its brilliant play'}
        ],
        'famous_openings': ['Sicilian Defense', 'Ruy Lopez'],
        'achievements': ['major tournaments'],
        'quotes': []
    }
    
    # Get Wikipedia information
    try:
        wiki_info = get_wikipedia_info(name)
        # Update our info with Wikipedia data
        if wiki_info:
            for key, value in wiki_info.items():
                if key in master_info and value:
                    master_info[key] = value
    except Exception as e:
        print(f"⚠️ Error getting Wikipedia info: {e}")
    
    # Get chess database information
    try:
        chess_db_info = get_chess_database_info(name)
        # Update with additional chess details
        if chess_db_info:
            for key, value in chess_db_info.items():
                if key in master_info and value:
                    master_info[key] = value
    except Exception as e:
        print(f"⚠️ Error getting chess database info: {e}")
    
    # Use AI to fill in missing details or enhance existing ones
    try:
        enhanced_info = enhance_with_ai(master_info)
        if enhanced_info:
            master_info = enhanced_info
    except Exception as e:
        print(f"⚠️ Error enhancing master info: {e}")
    
    return master_info

def get_wikipedia_info(name):
    """Get information about a chess player from Wikipedia"""
    # Format the name for Wikipedia URL
    wiki_name = name.replace(' ', '_')
    url = f"https://en.wikipedia.org/wiki/{wiki_name}"
    
    try:
        response = requests.get(url)
        soup = BeautifulSoup(response.text, 'html.parser')
        
        # Extract the main content
        content = soup.find(id="mw-content-text")
        if not content:
            return {}
        
        # Get the first few paragraphs (biography)
        paragraphs = content.find_all('p')
        biography = '\n'.join([p.get_text() for p in paragraphs[:5]])
        
        # Extract basic info from infobox
        infobox = soup.find('table', class_='infobox')
        info = {}
        
        if infobox:
            rows = infobox.find_all('tr')
            for row in rows:
                header = row.find('th')
                data = row.find('td')
                if header and data:
                    info[header.get_text().strip()] = data.get_text().strip()
        
        # Use AI to extract structured information from the biography
        player_info = extract_player_info_with_ai(biography, info)
        return player_info
        
    except Exception as e:
        print(f"Error fetching Wikipedia info: {e}")
        return {}

def extract_player_info_with_ai(biography, infobox_data):
    """Use AI to extract structured information about the chess player"""
    try:
        prompt = f"""
        Extract structured information about this chess player from the following Wikipedia text and infobox data.
        
        Text:
        {biography}
        
        Infobox data:
        {json.dumps(infobox_data, indent=2)}
        
        Return a JSON object with these fields (when available):
        - birth_year: Year of birth (as string)
        - death_year: Year of death if applicable (as string)
        - nationality: Country of origin
        - world_champion: Boolean indicating if they were world champion
        - world_champion_years: Years they were world champion (as string)
        - style_keywords: List of 3-5 strings that characterize their chess style
        - style_description: Brief description of their playing style
        - teaching_approach: How they would approach teaching chess
        - famous_openings: List of strings with openings they were known for playing
        - achievements: List of strings with notable chess achievements
        - quotes: List of strings with any famous quotes by or about them
        """
        
        response = openai.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": "You extract structured information about chess grandmasters from text."},
                {"role": "user", "content": prompt}
            ],
            response_format={"type": "json_object"}
        )
        
        # Parse the JSON response
        player_info = json.loads(response.choices[0].message.content)
        return player_info
        
    except Exception as e:
        print(f"Error extracting player info with AI: {e}")
        return {}

def get_chess_database_info(name):
    """Get game information from chess databases"""
    # This is a placeholder - you would implement API calls to chess.com, chessgames.com, etc.
    # For now, we'll simulate this with an AI call to get famous games
    
    try:
        response = openai.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": "You provide accurate information about famous chess games."},
                {"role": "user", "content": f"List 3 of {name}'s most famous chess games. For each game, include: 1) Opponent name, 2) Year/tournament (as string), 3) Opening played, 4) Why the game is famous, 5) A few key moves or moments. Format as JSON with a 'games' array containing objects with these fields: opponent, year, tournament, opening, why_famous."}
            ],
            response_format={"type": "json_object"}
        )
        
        # Parse the response
        result = json.loads(response.choices[0].message.content)
        famous_games = result.get("games", [])
        
        # Ensure all fields are strings
        for game in famous_games:
            for key, value in game.items():
                if value is not None and not isinstance(value, str):
                    game[key] = str(value)
        
        return {"famous_games": famous_games}
        
    except Exception as e:
        print(f"Error getting chess database info: {e}")
        return {"famous_games": []}

def enhance_with_ai(master_info):
    """Use AI to enhance and fill gaps in the grandmaster information"""
    try:
        # Create a prompt describing what we have and what we need
        prompt = f"""
        I have the following information about chess grandmaster {master_info['name']}, but some fields may be missing or incomplete.
        
        Current information:
        {json.dumps(master_info, indent=2)}
        
        Please enhance this information, filling in any missing fields with accurate data. Be especially detailed with:
        1. Their distinctive playing style (what made them unique)
        2. How they would approach teaching chess (based on their personality)
        3. Famous games they played (with accurate details)
        4. Their opening preferences
        
        For any fields with dates or years, please return them as strings, not numbers.
        For the famous_games array, make sure each game object has these fields as strings: opponent, year, tournament, opening, why_famous.
        
        Return the complete JSON object with all fields filled.
        """
        
        response = openai.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": "You provide accurate, detailed information about chess grandmasters."},
                {"role": "user", "content": prompt}
            ],
            response_format={"type": "json_object"}
        )
        
        # Parse and update the information
        enhanced_info = json.loads(response.choices[0].message.content)
        
        # Ensure all year fields are strings
        for year_field in ['birth_year', 'death_year', 'world_champion_years']:
            if year_field in enhanced_info and enhanced_info[year_field] is not None:
                enhanced_info[year_field] = str(enhanced_info[year_field])
        
        # Ensure famous games have the right structure and string values
        if 'famous_games' in enhanced_info:
            games = enhanced_info['famous_games']
            for game in games:
                for key, value in game.items():
                    if value is not None and not isinstance(value, str):
                        game[key] = str(value)
        
        # Ensure famous_openings is a list of strings
        if 'famous_openings' in enhanced_info:
            openings = enhanced_info['famous_openings']
            if isinstance(openings, list):
                opening_list = []
                for opening in openings:
                    if isinstance(opening, dict) and 'name' in opening:
                        opening_list.append(str(opening['name']))
                    elif isinstance(opening, str):
                        opening_list.append(opening)
                    else:
                        opening_list.append(str(opening))
                enhanced_info['famous_openings'] = opening_list
        
        return enhanced_info
        
    except Exception as e:
        print(f"Error enhancing master info: {e}")
        traceback.print_exc()
        return master_info

def create_game_analysis_example(name, system_message, info):
    """Create an example of the master analyzing a chess position"""
    style_keywords = info.get('style_keywords', ['tactical', 'strategic'])
    if not isinstance(style_keywords, list):
        style_keywords = ['tactical', 'strategic']
    
    # Ensure we have at least 2 keywords
    while len(style_keywords) < 2:
        style_keywords.append('strategic')
    
    # Handle if keywords aren't strings
    style_keywords = [str(keyword) for keyword in style_keywords]
    
    style_approach = str(info.get('style_description', 'distinctive'))
    
    return {
        "messages": [
            {"role": "system", "content": system_message},
            {"role": "user", "content": f"What do you think of this position? FEN: r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 0 1"},
            {"role": "assistant", "content": f"This is a standard position from the Ruy Lopez after 1.e4 e5 2.Nf3 Nc6. I would play 3.Bb5 here, putting pressure on the knight and preparing to castle. This position requires a balance of {' and '.join(style_keywords[:2])} understanding. In my approach to such positions, I focus on {style_approach} play. The key is to develop pieces harmoniously while maintaining central tension. White has slightly better development and should press this small advantage methodically."}
        ]
    }

def create_opening_philosophy_example(name, system_message, info):
    """Create an example of the master discussing opening philosophy"""
    # Get openings with fallback to defaults
    openings = info.get('famous_openings', ['Sicilian Defense', 'Ruy Lopez'])
    
    # Handle various possible formats of openings data
    opening_names = []
    if isinstance(openings, list):
        for opening in openings:
            if isinstance(opening, dict) and 'name' in opening:
                opening_names.append(str(opening['name']))
            elif isinstance(opening, str):
                opening_names.append(opening)
            else:
                opening_names.append(str(opening))
    elif isinstance(openings, str):
        opening_names = [openings]
    elif isinstance(openings, dict) and 'name' in openings:
        opening_names = [str(openings['name'])]
    
    # If we couldn't extract any names, use defaults
    if not opening_names:
        opening_names = ['Sicilian Defense', 'Ruy Lopez']
    
    return {
        "messages": [
            {"role": "system", "content": system_message},
            {"role": "user", "content": f"What was your approach to chess openings? Which ones did you prefer?"},
            {"role": "assistant", "content": f"In my approach to openings, I favored positions that led to the type of middlegames where my strengths could shine. I was particularly known for playing {', '.join(opening_names)}. When selecting an opening, I always emphasized understanding the underlying principles rather than memorizing variations. The opening sets the stage for your strategic plan throughout the game, so I chose systems that aligned with my preferred playing style and gave me the positions where I could outplay my opponents. What opening are you currently playing? I might have some specific advice for your repertoire."}
        ]
    }

def create_middlegame_advice_example(name, system_message, info):
    """Create an example of the master giving middlegame advice"""
    style_desc = str(info.get('style_description', 'tactical and strategic'))
    
    return {
        "messages": [
            {"role": "system", "content": system_message},
            {"role": "user", "content": f"How should I approach complex middlegame positions?"},
            {"role": "assistant", "content": f"In complex middlegame positions, I always focused on {style_desc} elements first. Start by evaluating the pawn structure - it's the skeleton that defines the character of the position. Look for weaknesses you can target and strengths you can build upon. Always consider piece coordination and how to improve your worst-placed piece. When calculating variations, focus on candidate moves that align with the position's strategic demands. Remember that the middlegame is where chess personality truly emerges - develop a playing style that feels natural to you while respecting the objective requirements of each position."}
        ]
    }

def create_endgame_technique_example(name, system_message, info):
    """Create an example of the master discussing endgame technique"""
    return {
        "messages": [
            {"role": "system", "content": system_message},
            {"role": "user", "content": f"What's your advice for improving endgame play?"},
            {"role": "assistant", "content": f"Endgame excellence comes from understanding fundamental positions and principles. First, study the essential theoretical endgames: king and pawn vs. king, rook endgames, and opposite-colored bishop endings. The key to my endgame success was precise calculation combined with clear understanding of the position's character. Always activate your king immediately in the endgame - it transforms from a piece needing protection to a powerful attacker. Pay attention to pawn structure, as it often determines the outcome. Practice regularly with endgame studies, which will develop your calculation accuracy and pattern recognition. Most importantly, realize that endgames require patience and precision - rushing can turn a winning position into a draw, or a draw into a loss."}
        ]
    }

def create_chess_history_example(name, system_message, info):
    """Create an example of the master discussing chess history"""
    birth = str(info.get('birth_year', ''))
    death = str(info.get('death_year', ''))
    
    # Extract achievements
    achievements = info.get('achievements', ['World Chess Championship'])
    if not isinstance(achievements, list):
        if isinstance(achievements, str):
            achievements = [achievements]
        else:
            achievements = ['World Chess Championship']
    
    # Convert any non-string achievements to strings
    achievements = [str(achievement) for achievement in achievements]
    
    lifespan = f"({birth}-{death})" if birth and death else ""
    
    return {
        "messages": [
            {"role": "system", "content": system_message},
            {"role": "user", "content": f"Tell me about your chess career and achievements."},
            {"role": "assistant", "content": f"My chess career {lifespan} was defined by my distinctive style and approach to the game. Among my greatest achievements were {', '.join(achievements)}. What set me apart from other players of my era was my approach to chess - I combined deep strategic understanding with tactical precision. Throughout my career, I faced many legendary opponents, each presenting unique challenges that helped shape my evolution as a player. The chess world was quite different in my era compared to today's computer-dominated environment. We had to rely much more on intuition, analysis skills, and deep understanding of chess principles rather than memorized computer lines."}
        ]
    }

def create_famous_game_example(name, system_message, game_info):
    """Create an example of the master discussing one of their famous games"""
    # Safely extract game information with string conversion and default values
    opponent = str(game_info.get('opponent', 'a strong grandmaster'))
    year = str(game_info.get('year', ''))
    event = str(game_info.get('tournament', ''))
    opening = str(game_info.get('opening', 'a complex opening'))
    why_famous = str(game_info.get('why_famous', 'its brilliant combinations'))
    
    # Create location string safely
    if event and year:
        location = f"{event}, {year}"
    elif event:
        location = event
    elif year:
        location = year
    else:
        location = ""
    
    # Create the content string with proper string interpolation
    location_suffix = f" in {location}" if location else ""
    user_content = f"Can you tell me about your famous game against {opponent}{location_suffix}?"
    
    return {
        "messages": [
            {"role": "system", "content": system_message},
            {"role": "user", "content": user_content},
            {"role": "assistant", "content": f"My game against {opponent}{location_suffix} is indeed one of my most memorable. We played {opening}, which led to the type of position I relished. What made this game special was {why_famous}. The critical moment came when I demonstrated my characteristic style with a move that surprised many observers. This game showcased my approach to chess: focusing on piece activity, dynamic play, and creating practical problems for my opponent. When studying this game, pay attention to how the strategic elements and tactical opportunities intertwined - that's the essence of high-level chess. Would you like me to analyze any specific position from this game?"}
        ]
    }

def create_general_teaching_example(name, system_message, info, seed=0):
    """Create a general teaching example in the master's style"""
    style = str(info.get('style_description', 'dynamic and precise'))
    
    # Create a list of potential chess questions
    questions = [
        "How can I improve my tactical vision?",
        "What's the most important principle in chess?",
        "How should I study chess to improve quickly?",
        "What separates grandmasters from amateur players?",
        "How important is opening theory?",
        "What's your advice for tournament play?",
        "How do you handle pressure in critical positions?",
        "What books would you recommend for improvement?",
        "How should I analyze my own games?",
        "What's the best way to learn from my mistakes?"
    ]
    
    # Use AI to generate a response in the master's style
    try:
        # Use both name and seed to select a question, ensuring variety
        question_index = abs(hash(name + str(seed))) % len(questions)
        question = questions[question_index]
        
        response = openai.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": f"You are {name}, a chess grandmaster with a {style} style. Answer this chess question in your authentic voice, sharing your wisdom and experience."},
                {"role": "user", "content": question}
            ],
            max_tokens=300
        )
        
        answer = response.choices[0].message.content
        
        return {
            "messages": [
                {"role": "system", "content": system_message},
                {"role": "user", "content": question},
                {"role": "assistant", "content": answer}
            ]
        }
    except Exception as e:
        print(f"Error creating general teaching example: {e}")
        # Return a safe fallback
        return {
            "messages": [
                {"role": "system", "content": system_message},
                {"role": "user", "content": "What's your most important advice for chess improvement?"},
                {"role": "assistant", "content": f"The key to chess improvement lies in understanding the game's fundamental principles, not just memorizing variations. In my experience, players grow most when they study both strategy and tactics in balance. Analyze your games honestly, learn from your mistakes, and focus on the areas where you struggle most. Chess is a journey of continuous learning, and even at the highest levels, there's always room for growth. What specific area of your game are you looking to develop?"}
            ]
        }

def create_chess_masters_collection(masters_list):
    """Create training data for multiple chess grandmasters"""
    all_examples = {}
    successful_masters = 0
    
    for master in masters_list:
        try:
            examples = create_chess_master_training_data(master)
            if examples:
                all_examples[master] = examples
                successful_masters += 1
            
            # Be nice to the API and avoid rate limits
            time.sleep(5)
        except Exception as e:
            print(f"❌ Failed to process {master}: {e}")
            traceback.print_exc()
    
    # Combine all examples into one file if needed
    try:
        with open("all_chess_masters.jsonl", "w") as f:
            for master, examples in all_examples.items():
                for example in examples:
                    example_with_master = example.copy()
                    example_with_master["master"] = master
                    f.write(json.dumps(example_with_master) + "\n")
        
        print(f"✨ Successfully created training data for {successful_masters} out of {len(masters_list)} chess masters!")
    except Exception as e:
        print(f"❌ Error saving combined file: {e}")
    
    return all_examples

# Example usage
if __name__ == "__main__":
    # List of chess masters to create training data for
    masters = [
        "Alexander Alekhine",
        "Aron Nimzowitsch",
        "Hikaru Nakamura",  # Fixed spelling
        "Wilhelm Steinitz",
        "Boris Spassky",
        "Tigran Petrosian",
        "Ding Liren",
        "Paul Keres",
        "Viktor Korchnoi",
        "David Bronstein"
    ]
    
    create_chess_masters_collection(masters)