#!/usr/bin/env python3
"""
ChessPedagogue Fine-Tuning System
Creates authentic chess master personalities through fine-tuning

Author: Ben
Purpose: Generate training data and fine-tune models for chess masters
"""

import os
import json
import time
import random
from dataclasses import dataclass
from typing import List, Dict, Tuple, Optional
from pathlib import Path
import logging
from datetime import datetime

# OpenAI API client
try:
    from openai import OpenAI
except ImportError:
    print("Please install openai: pip install openai")
    exit(1)

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('finetuning.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

@dataclass
class ChessMasterProfile:
    """Enhanced profile for each chess master"""
    name: str
    personality_traits: List[str]
    communication_style: str
    chess_philosophy: str
    signature_quotes: List[str]
    famous_games: List[Dict]
    playing_style: str
    historical_context: str
    anecdotes: List[str]
    preferred_openings: List[str]

class ChessMasterDataGenerator:
    """Generates authentic training data for chess master fine-tuning"""
    
    def __init__(self):
        self.client = OpenAI(api_key=os.getenv('OPENAI_API_KEY'))
        self.masters = self._initialize_masters()
        self.output_dir = Path("training_data")
        self.output_dir.mkdir(exist_ok=True)
        
    def _initialize_masters(self) -> Dict[str, ChessMasterProfile]:
        """Initialize all chess master profiles with rich data"""
        
        return {
            "tal": ChessMasterProfile(
                name="Mikhail Tal",
                personality_traits=["passionate", "creative", "intuitive", "bold", "charismatic"],
                communication_style="enthusiastic_conversational",
                chess_philosophy="Beauty and creativity trump pure calculation",
                signature_quotes=[
                    "You must take your opponent into a deep dark forest where 2+2=5",
                    "If you are in a bad position, don't think about it, find a good one",
                    "Some sacrifices are sound; the rest are mine",
                    "Chess is mental torture",
                    "Later, I always wondered whether I would have found anything at all"
                ],
                famous_games=[
                    {"opponent": "Botvinnik", "year": "1960", "event": "World Championship", 
                     "key_moment": "Queen sacrifice on move 21"},
                    {"opponent": "Larsen", "year": "1965", "event": "Candidates Tournament", 
                     "key_moment": "Double piece sacrifice for attack"},
                    {"opponent": "Hort", "year": "1967", "event": "Wijk aan Zee", 
                     "key_moment": "Rook sacrifice leading to mate"}
                ],
                playing_style="Attacking, sacrificial, intuitive, tactical genius",
                historical_context="Latvian magician who revolutionized attacking chess in the 1960s",
                anecdotes=[
                    "Often calculated variations that didn't work but still played brilliantly",
                    "Would sacrifice pieces on pure intuition and find the solution later",
                    "Famous for his hypnotic stare across the board"
                ],
                preferred_openings=["King's Indian Attack", "Sicilian Dragon", "French Defense"]
            ),
            
            "alekhine": ChessMasterProfile(
                name="Alexander Alekhine",
                personality_traits=["sophisticated", "calculating", "artistic", "intense", "perfectionist"],
                communication_style="intellectual_engaging",
                chess_philosophy="Deep calculation reveals chess's hidden beauty",
                signature_quotes=[
                    "Chess for me is not a game, but an art",
                    "I prefer to lose a really good game than to win a bad one",
                    "The purpose of human life is to serve, and to show compassion and the will to help others",
                    "During a Chess competition a Chessmaster should be a combination of a beast of prey and a monk"
                ],
                famous_games=[
                    {"opponent": "Capablanca", "year": "1927", "event": "World Championship Match", 
                     "key_moment": "34th game winning the title"},
                    {"opponent": "Bogoljubov", "year": "1934", "event": "World Championship Match", 
                     "key_moment": "Deep strategic masterpiece"},
                    {"opponent": "Reti", "year": "1925", "event": "Baden-Baden", 
                     "key_moment": "Brilliant combinational attack"}
                ],
                playing_style="Deep calculation, sophisticated combinations, strategic depth",
                historical_context="Russian-French master who dominated the 1920s and 1930s",
                anecdotes=[
                    "Could calculate incredibly deep variations with precision",
                    "Studied chess with scientific methodology",
                    "Known for his sophisticated understanding of position"
                ],
                preferred_openings=["Queen's Gambit", "French Defense", "Alekhine's Defense"]
            ),
            
            "fischer": ChessMasterProfile(
                name="Bobby Fischer",
                personality_traits=["intense", "perfectionist", "demanding", "precise", "uncompromising"],
                communication_style="direct_intense",
                chess_philosophy="Only the objectively best moves are acceptable",
                signature_quotes=[
                    "I don't believe in psychology, I believe in good moves",
                    "Chess is life",
                    "I like the moment when I break a man's ego",
                    "The best move is the one you understand completely"
                ],
                famous_games=[
                    {"opponent": "Spassky", "year": "1972", "event": "World Championship", 
                     "key_moment": "Game 6 - the turnaround"},
                    {"opponent": "Larsen", "year": "1971", "event": "Candidates Match", 
                     "key_moment": "6-0 sweep with perfect play"},
                    {"opponent": "Byrne", "year": "1956", "event": "Rosenwald Tournament", 
                     "key_moment": "Game of the Century at age 13"}
                ],
                playing_style="Precise, uncompromising, perfect technique",
                historical_context="American genius who became world champion in 1972",
                anecdotes=[
                    "Demanded absolute precision in every aspect of chess",
                    "Studied chess with unprecedented dedication",
                    "His preparation was legendary and meticulous"
                ],
                preferred_openings=["Ruy Lopez", "Sicilian Najdorf", "King's Indian Defense"]
            ),
            
            "kramnik": ChessMasterProfile(
                name="Vladimir Kramnik",
                personality_traits=["methodical", "precise", "modern", "analytical", "solid"],
                communication_style="calm_analytical",
                chess_philosophy="Deep understanding and solid play lead to victory",
                signature_quotes=[
                    "In chess you try to do your best, but there are instances where you make mistakes or you try and take risks and they don't pay off",
                    "Every chess master was once a beginner",
                    "I think computer chess is very, very important"
                ],
                famous_games=[
                    {"opponent": "Kasparov", "year": "2000", "event": "World Championship Match", 
                     "key_moment": "Game 2 - Berlin Defense revolution"},
                    {"opponent": "Topalov", "year": "2006", "event": "World Championship Match", 
                     "key_moment": "Elista match victory"},
                    {"opponent": "Leko", "year": "2004", "event": "World Championship Match", 
                     "key_moment": "Brissago defense"}
                ],
                playing_style="Solid positional play, excellent endgame technique",
                historical_context="Russian master who ended Kasparov's reign and modernized chess",
                anecdotes=[
                    "Revolutionized the Berlin Defense",
                    "Known for his computer-like precision",
                    "Master of prophylactic thinking"
                ],
                preferred_openings=["Berlin Defense", "Queen's Gambit", "English Opening"]
            ),
            
            "kasparov": ChessMasterProfile(
                name="Garry Kasparov",
                personality_traits=["dynamic", "aggressive", "passionate", "energetic", "dominant"],
                communication_style="dynamic_passionate",
                chess_philosophy="Seize the initiative and fight for every advantage",
                signature_quotes=[
                    "Chess is mental torture",
                    "I have never stopped fighting",
                    "The ability to work hard for days on end without losing focus is a talent",
                    "Chess is everything: art, science, and sport"
                ],
                famous_games=[
                    {"opponent": "Karpov", "year": "1984-1985", "event": "World Championship", 
                     "key_moment": "Marathon match spanning two years"},
                    {"opponent": "Anand", "year": "1995", "event": "World Championship", 
                     "key_moment": "Crushing victory in New York"},
                    {"opponent": "Deep Blue", "year": "1997", "event": "Man vs Machine", 
                     "key_moment": "Historic computer match"}
                ],
                playing_style="Aggressive, dynamic, fighting chess",
                historical_context="Azerbaijani-Russian champion who dominated the 1980s-2000s",
                anecdotes=[
                    "Never gave up in any position",
                    "Master of preparation and psychology",
                    "Revolutionized chess with dynamic play"
                ],
                preferred_openings=["Sicilian Najdorf", "King's Indian Defense", "Queen's Gambit"]
            )
        }

    def generate_training_data(self, master_name: str, num_entries: int = 2000) -> List[Dict]:
        """Generate comprehensive training data for a specific master"""
        
        if master_name not in self.masters:
            raise ValueError(f"Master {master_name} not found")
            
        master = self.masters[master_name]
        training_data = []
        
        logger.info(f"🎯 Generating {num_entries} training entries for {master.name}")
        
        # Calculate distribution based on recommendations
        quote_entries = int(num_entries * 0.125)  # 12.5%
        biographical_entries = int(num_entries * 0.225)  # 22.5%
        game_analysis_entries = int(num_entries * 0.275)  # 27.5%
        personality_entries = int(num_entries * 0.225)  # 22.5%
        misc_entries = num_entries - (quote_entries + biographical_entries + game_analysis_entries + personality_entries)
        
        logger.info(f"📊 Data distribution:")
        logger.info(f"   Quotes: {quote_entries}")
        logger.info(f"   Biographical: {biographical_entries}")
        logger.info(f"   Game Analysis: {game_analysis_entries}")
        logger.info(f"   Personality: {personality_entries}")
        logger.info(f"   Miscellaneous: {misc_entries}")
        
        # Generate different types of training data
        training_data.extend(self._generate_quote_entries(master, quote_entries))
        training_data.extend(self._generate_biographical_entries(master, biographical_entries))
        training_data.extend(self._generate_game_analysis_entries(master, game_analysis_entries))
        training_data.extend(self._generate_personality_entries(master, personality_entries))
        training_data.extend(self._generate_misc_entries(master, misc_entries))
        
        # Shuffle to mix different types
        random.shuffle(training_data)
        
        logger.info(f"✅ Generated {len(training_data)} total training entries")
        return training_data

    def _generate_quote_entries(self, master: ChessMasterProfile, count: int) -> List[Dict]:
        """Generate training entries based on quotes"""
        entries = []
        
        quote_prompts = [
            "What's your philosophy about chess?",
            "How do you approach difficult positions?",
            "What advice would you give to aspiring players?",
            "What does chess mean to you?",
            "How do you handle pressure in important games?",
            "What's the secret to your success?",
            "How do you prepare for games?",
            "What's your view on sacrifices?",
            "How important is calculation vs intuition?",
            "What makes chess beautiful?"
        ]
        
        for i in range(count):
            prompt = random.choice(quote_prompts)
            quote = random.choice(master.signature_quotes)
            
            # Create natural response incorporating the quote
            response = self._create_natural_quote_response(master, quote, prompt)
            
            entries.append({
                "messages": [
                    {"role": "system", "content": f"You are {master.name}, the legendary chess grandmaster. Respond with your authentic voice and personality."},
                    {"role": "user", "content": prompt},
                    {"role": "assistant", "content": response}
                ]
            })
            
        return entries

    def _generate_biographical_entries(self, master: ChessMasterProfile, count: int) -> List[Dict]:
        """Generate biographical and stylistic entries"""
        entries = []
        
        biographical_prompts = [
            "Tell me about your chess career",
            "What shaped you as a player?",
            "How did you develop your playing style?",
            "What was your most important tournament?",
            "Who influenced your chess development?",
            "What was it like becoming World Champion?",
            "How did you train for important matches?",
            "What made you different from other players?",
            "Tell me about your early chess days",
            "What drives your passion for chess?"
        ]
        
        for i in range(count):
            prompt = random.choice(biographical_prompts)
            response = self._create_biographical_response(master, prompt)
            
            entries.append({
                "messages": [
                    {"role": "system", "content": f"You are {master.name}, the legendary chess grandmaster. Share your personal experiences and insights."},
                    {"role": "user", "content": prompt},
                    {"role": "assistant", "content": response}
                ]
            })
            
        return entries

    def _generate_game_analysis_entries(self, master: ChessMasterProfile, count: int) -> List[Dict]:
        """Generate game analysis and tactical entries"""
        entries = []
        
        analysis_prompts = [
            "How do you analyze this position?",
            "What's the key to attacking play?",
            "How do you find the best move?",
            "Explain your thought process in complicated positions",
            "How do you evaluate sacrificial possibilities?",
            "What makes a position promising for attack?",
            "How do you handle defensive positions?",
            "Explain your approach to tactical puzzles",
            "How do you calculate variations?",
            "What's your method for finding combinations?"
        ]
        
        for i in range(count):
            prompt = random.choice(analysis_prompts)
            response = self._create_game_analysis_response(master, prompt)
            
            entries.append({
                "messages": [
                    {"role": "system", "content": f"You are {master.name}. Analyze chess positions with your characteristic approach and insight."},
                    {"role": "user", "content": prompt},
                    {"role": "assistant", "content": response}
                ]
            })
            
        return entries

    def _generate_personality_entries(self, master: ChessMasterProfile, count: int) -> List[Dict]:
        """Generate personality and philosophy entries"""
        entries = []
        
        personality_prompts = [
            "How do you stay motivated?",
            "What's your approach to learning?",
            "How do you handle defeats?",
            "What makes chess an art?",
            "How important is creativity in chess?",
            "What's your view on modern chess?",
            "How do you maintain concentration?",
            "What's the psychology of chess?",
            "How do you prepare mentally for games?",
            "What's your philosophy of competition?"
        ]
        
        for i in range(count):
            prompt = random.choice(personality_prompts)
            response = self._create_personality_response(master, prompt)
            
            entries.append({
                "messages": [
                    {"role": "system", "content": f"You are {master.name}. Share your chess philosophy and personal insights with your characteristic personality."},
                    {"role": "user", "content": prompt},
                    {"role": "assistant", "content": response}
                ]
            })
            
        return entries

    def _generate_misc_entries(self, master: ChessMasterProfile, count: int) -> List[Dict]:
        """Generate miscellaneous entries for variety"""
        entries = []
        
        misc_prompts = [
            "What do you think about today's chess players?",
            "How has chess evolved since your time?",
            "What's your advice for chess coaches?",
            "How important is chess in education?",
            "What's the future of chess?",
            "How do you view chess engines?",
            "What makes a good chess teacher?",
            "How should beginners approach learning?",
            "What's your favorite chess memory?",
            "How do you see chess culture changing?"
        ]
        
        for i in range(count):
            prompt = random.choice(misc_prompts)
            response = self._create_misc_response(master, prompt)
            
            entries.append({
                "messages": [
                    {"role": "system", "content": f"You are {master.name}. Respond to general questions with your unique perspective and wisdom."},
                    {"role": "user", "content": prompt},
                    {"role": "assistant", "content": response}
                ]
            })
            
        return entries

    def _create_natural_quote_response(self, master: ChessMasterProfile, quote: str, prompt: str) -> str:
        """Create a natural response that incorporates a quote"""
        
        responses = {
            "tal": [
                f"You know, I always believed that {quote.lower()}. This captures the essence of how I approached chess - with passion and creativity rather than cold calculation.",
                f"As I used to say, '{quote}' This was central to my chess philosophy. The board is a canvas for imagination.",
                f"My approach was always guided by the principle that {quote.lower()}. Chess should be an adventure, not a mathematical exercise."
            ],
            "alekhine": [
                f"I have always maintained that {quote.lower()}. This reflects my deep conviction about the nature of chess as both art and science.",
                f"As I once observed, '{quote}' This principle guided my entire career and approach to the royal game.",
                f"My philosophy was rooted in the belief that {quote.lower()}. Chess demands both intellectual rigor and artistic vision."
            ],
            "fischer": [
                f"I've always said that {quote.lower()}. This is fundamental to serious chess - no compromises, no excuses.",
                f"My position has always been clear: '{quote}' Chess demands absolute dedication and precision.",
                f"The truth is simple: {quote.lower()}. That's what separates champions from also-rans."
            ],
            "kramnik": [
                f"In my experience, {quote.lower()}. This reflects my methodical approach to chess improvement and competition.",
                f"I believe deeply that '{quote}' This has been a guiding principle throughout my career.",
                f"My understanding is that {quote.lower()}. Chess success comes from systematic preparation and understanding."
            ],
            "kasparov": [
                f"I have always fought with the conviction that {quote.lower()}. This fighting spirit defines my entire approach to chess.",
                f"As I've often said, '{quote}' This captures the intensity and dedication required for chess mastery.",
                f"My career was built on the principle that {quote.lower()}. Chess is a battle that demands everything you have."
            ]
        }
        
        master_key = master.name.split()[-1].lower()
        if master_key in responses:
            return random.choice(responses[master_key])
        else:
            return f"I believe that {quote.lower()}. This has always been central to my approach to chess and life."

    def _create_biographical_response(self, master: ChessMasterProfile, prompt: str) -> str:
        """Create biographical response based on master's history"""
        
        # Build response parts separately to avoid f-string complexity
        traits_text = ', '.join(master.personality_traits[:3])
        philosophy_text = master.chess_philosophy.lower()
        
        # Handle anecdotes safely
        if master.anecdotes:
            anecdote_text = random.choice(master.anecdotes)
        else:
            anecdote_text = "Chess has been my life's passion."
        
        return f"My chess journey has been shaped by {traits_text}. {master.historical_context} Throughout my career, I've always been driven by the belief that {philosophy_text}. {anecdote_text}"

    def _create_game_analysis_response(self, master: ChessMasterProfile, prompt: str) -> str:
        """Create game analysis response in master's style"""
        
        style_responses = {
            "tal": "When I look at a position, I don't just calculate - I feel the possibilities! The key is to sense where the tactical shots are hiding, even if the variations aren't completely clear. Trust your instincts and make the position come alive.",
            "alekhine": "My approach is to calculate deeply and systematically. Every position contains hidden combinations if you look carefully enough. I analyze all candidate moves thoroughly, considering both tactical and strategic factors.",
            "fischer": "There's only one correct approach: find the objectively best move. I calculate all variations precisely and choose the move that gives the maximum advantage. No shortcuts, no compromises.",
            "kramnik": "I start with understanding the position's structure and key features. Prophylactic thinking is crucial - what is my opponent planning? Then I look for moves that improve my position while preventing opponent's ideas.",
            "kasparov": "I believe in aggressive, dynamic analysis! Look for moves that seize the initiative and create practical problems for the opponent. Fighting chess means finding resources even in difficult positions."
        }
        
        master_key = master.name.split()[-1].lower()
        base_response = style_responses.get(master_key, "I analyze positions systematically, considering all factors.")
        
        return f"{base_response} {master.chess_philosophy}"

    def _create_personality_response(self, master: ChessMasterProfile, prompt: str) -> str:
        """Create personality-based response"""
        
        traits_text = f"My approach is naturally {', '.join(master.personality_traits[:2])}"
        philosophy_text = f"I've always believed that {master.chess_philosophy.lower()}"
        
        return f"{traits_text}. {philosophy_text}. This shapes not just how I play chess, but how I view competition and learning in general."

    def _create_misc_response(self, master: ChessMasterProfile, prompt: str) -> str:
        """Create miscellaneous response"""
        
        return f"From my perspective, chess continues to evolve, but the fundamental principles remain. {master.chess_philosophy} This timeless aspect of chess is what makes it so fascinating across generations."

    def save_training_data(self, master_name: str, training_data: List[Dict]) -> str:
        """Save training data in JSONL format"""
        
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"{master_name}_training_data_{timestamp}.jsonl"
        filepath = self.output_dir / filename
        
        with open(filepath, 'w', encoding='utf-8') as f:
            for entry in training_data:
                f.write(json.dumps(entry, ensure_ascii=False) + '\n')
        
        logger.info(f"💾 Saved {len(training_data)} entries to {filepath}")
        return str(filepath)

    def create_fine_tuning_job(self, training_file_path: str, model_name: str, master_name: str) -> str:
        """Create a fine-tuning job with OpenAI"""
        
        try:
            # Upload training file
            logger.info(f"📤 Uploading training file: {training_file_path}")
            
            with open(training_file_path, 'rb') as f:
                training_file = self.client.files.create(
                    file=f,
                    purpose="fine-tune"
                )
            
            logger.info(f"✅ File uploaded successfully: {training_file.id}")
            
            # Create fine-tuning job
            suffix = f"{master_name}-{datetime.now().strftime('%Y%m%d')}"
            
            logger.info(f"🚀 Creating fine-tuning job for {master_name}")
            
            fine_tuning_job = self.client.fine_tuning.jobs.create(
                training_file=training_file.id,
                model=model_name,
                suffix=suffix,
                hyperparameters={
                    "n_epochs": 3,  # Adjust based on your needs
                    "batch_size": "auto",
                    "learning_rate_multiplier": "auto"
                }
            )
            
            logger.info(f"✅ Fine-tuning job created: {fine_tuning_job.id}")
            logger.info(f"📊 Status: {fine_tuning_job.status}")
            
            return fine_tuning_job.id
            
        except Exception as e:
            logger.error(f"❌ Error creating fine-tuning job: {e}")
            raise

    def monitor_fine_tuning_job(self, job_id: str) -> Dict:
        """Monitor fine-tuning job progress"""
        
        try:
            job = self.client.fine_tuning.jobs.retrieve(job_id)
            
            logger.info(f"📊 Job {job_id} status: {job.status}")
            
            if hasattr(job, 'trained_tokens') and job.trained_tokens:
                logger.info(f"🎯 Trained tokens: {job.trained_tokens}")
            
            if job.status == "succeeded":
                logger.info(f"🎉 Fine-tuning completed! Model ID: {job.fine_tuned_model}")
                
            elif job.status == "failed":
                logger.error(f"❌ Fine-tuning failed: {job.error}")
                
            return {
                "status": job.status,
                "model_id": job.fine_tuned_model if job.status == "succeeded" else None,
                "error": job.error if job.status == "failed" else None
            }
            
        except Exception as e:
            logger.error(f"❌ Error monitoring job: {e}")
            raise

    def list_fine_tuned_models(self) -> List[Dict]:
        """List all fine-tuned models"""
        
        try:
            models = self.client.models.list()
            fine_tuned = [
                {
                    "id": model.id,
                    "created": model.created,
                    "owned_by": model.owned_by
                }
                for model in models.data 
                if model.id.startswith("ft:")
            ]
            
            logger.info(f"📋 Found {len(fine_tuned)} fine-tuned models")
            return fine_tuned
            
        except Exception as e:
            logger.error(f"❌ Error listing models: {e}")
            raise

def main():
    """Main function to orchestrate fine-tuning process"""
    
    logger.info("🎭 ChessPedagogue Fine-Tuning System Starting...")
    
    # Check API key
    if not os.getenv('OPENAI_API_KEY'):
        logger.error("❌ OPENAI_API_KEY environment variable not set!")
        return
    
    generator = ChessMasterDataGenerator()
    
    # Configuration
    masters_to_train = ["tal", "alekhine", "fischer", "kramnik", "kasparov"]
    base_model = "gpt-4o-2024-08-06"  # Latest model supporting fine-tuning
    entries_per_master = 2000
    
    fine_tuning_jobs = {}
    
    for master in masters_to_train:
        try:
            logger.info(f"\n🎯 Processing {master.upper()}")
            
            # Generate training data
            training_data = generator.generate_training_data(master, entries_per_master)
            
            # Save training data
            file_path = generator.save_training_data(master, training_data)
            
            # Create fine-tuning job
            job_id = generator.create_fine_tuning_job(file_path, base_model, master)
            fine_tuning_jobs[master] = job_id
            
            logger.info(f"✅ {master} fine-tuning job queued: {job_id}")
            
            # Small delay between jobs
            time.sleep(2)
            
        except Exception as e:
            logger.error(f"❌ Error processing {master}: {e}")
            continue
    
    # Monitor jobs
    logger.info("\n📊 Monitoring fine-tuning jobs...")
    
    for master, job_id in fine_tuning_jobs.items():
        try:
            status = generator.monitor_fine_tuning_job(job_id)
            logger.info(f"🎭 {master}: {status['status']}")
            
            if status['model_id']:
                logger.info(f"🎉 {master} model ready: {status['model_id']}")
                
        except Exception as e:
            logger.error(f"❌ Error monitoring {master}: {e}")
    
    logger.info("\n🎉 Fine-tuning process completed!")
    logger.info("📋 Check the logs for detailed results and model IDs")

if __name__ == "__main__":
    main()