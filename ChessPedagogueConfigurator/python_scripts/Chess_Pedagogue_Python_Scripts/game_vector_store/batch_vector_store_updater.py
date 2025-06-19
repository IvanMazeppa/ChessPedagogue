import asyncio
import json
from pathlib import Path
from chunked_fen_db_creator_v9_fixed import FixedResponsesAPIChessCreator
import logging
from datetime import datetime

class BatchVectorStoreUpdater:
    """Batch processor for updating all chess master vector stores"""
    
    def __init__(self):
        self.setup_logging()
        self.master_configs = self.load_complete_master_configs()
        self.creator = FixedResponsesAPIChessCreator()
        
    def setup_logging(self):
        """Setup logging for batch processing"""
        log_format = '%(asctime)s - %(levelname)s - %(message)s'
        
        file_handler = logging.FileHandler(
            f'batch_update_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log',
            encoding='utf-8'
        )
        file_handler.setFormatter(logging.Formatter(log_format))
        
        console_handler = logging.StreamHandler()
        console_handler.setFormatter(logging.Formatter(log_format))
        
        logger = logging.getLogger(__name__)
        logger.setLevel(logging.INFO)
        logger.addHandler(file_handler)
        logger.addHandler(console_handler)
        
        self.logger = logger

    def load_complete_master_configs(self) -> dict:
        """Load complete chess master configurations with ALL vector store IDs"""
        return {
            "Magnus Carlsen": {
                "assistant_id": "asst_TTzxbfvJQz3e80FetQblJ0Gl",
                "vector_store_id": "vs_68365028eb988191b09d8d50e6f11b5d",
                "model": "ft:gpt-4o-mini-2024-07-18:personal:magnus-carlsen-chess-master:AIHj5PaY",
                "style_keywords": "endgame grinding, positional mastery, universal style, calculation depth",
                "games_count": 30,
                "priority": "high"
            },
            "Mikhail Tal": {
                "assistant_id": "asst_VKHRwLqOD8dqvQUmRq2vf8OL", 
                "vector_store_id": "vs_682f419a57288191aa3cd922b27acb5f",
                "model": "ft:gpt-4o-mini-2024-07-18:personal:mikhail-tal-chess-master:AHkZonl6",
                "style_keywords": "sacrificial brilliance, tactical genius, intuitive play, artistic combinations",
                "games_count": 35,
                "priority": "high"
            },
            "Bobby Fischer": {
                "assistant_id": "asst_s8LrE2n5qWCBF6ZLMeDNLmGd",
                "vector_store_id": "vs_kGfkR5qRFZO1VXBkVZ6TU9a3", 
                "model": "ft:gpt-4o-mini-2024-07-18:personal:bobby-fischer-chess-master:AIHjN5qJ",
                "style_keywords": "precise calculation, opening mastery, systematic approach, clarity",
                "games_count": 30,
                "priority": "high"
            },
            "Garry Kasparov": {
                "vector_store_id": "vs_NEED_ID",  # YOU NEED TO PROVIDE THESE
                "style_keywords": "dynamic play, computer analysis, deep preparation, fighting spirit",
                "games_count": 40,
                "priority": "high"
            },
            "Anatoly Karpov": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "positional mastery, strategic depth, endgame technique, patient maneuvering",
                "games_count": 35,
                "priority": "high"
            },
            "Vladimir Kramnik": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "solid positional play, Berlin Defense, endgame excellence, computer preparation",
                "games_count": 30,
                "priority": "medium"
            },
            "Jose Raul Capablanca": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "natural talent, simplicity, endgame mastery, effortless technique",
                "games_count": 25,
                "priority": "high"
            },
            "Emanuel Lasker": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "psychological warfare, fighting spirit, practical play, late-career brilliance",
                "games_count": 30,
                "priority": "medium"
            },
            "Paul Morphy": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "romantic chess, rapid development, tactical brilliance, natural genius",
                "games_count": 20,
                "priority": "medium"
            },
            "Viswanathan Anand": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "speed chess, versatile style, rapid calculation, world champion mentality",
                "games_count": 30,
                "priority": "medium"
            },
            "Mikhail Botvinnik": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "scientific approach, systematic preparation, strong technique, soviet school",
                "games_count": 25,
                "priority": "low"
            },
            "Alexander Alekhine": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "combinatorial genius, attacking brilliance, deep calculation, tactical artistry",
                "games_count": 30,
                "priority": "medium"
            },
            "Hikaru Nakamura": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "speed chess mastery, tactical alertness, modern streaming, online dominance",
                "games_count": 25,
                "priority": "medium"
            },
            "Gukesh Dommaraju": {
                "vector_store_id": "vs_NEED_ID", 
                "style_keywords": "youthful brilliance, modern preparation, engine-assisted analysis, rising star",
                "games_count": 20,
                "priority": "low"
            },
            "Nigel Short": {
                "vector_store_id": "vs_NEED_ID",
                "style_keywords": "fighting chess, tactical sharpness, English pragmatism, counterattacking",
                "games_count": 20,
                "priority": "low"
            }
        }

    def validate_vector_store_ids(self) -> bool:
        """Check if all vector store IDs are provided"""
        missing_ids = []
        for name, config in self.master_configs.items():
            if config.get("vector_store_id", "").startswith("vs_NEED_ID"):
                missing_ids.append(name)
        
        if missing_ids:
            print("❌ Missing vector store IDs for:")
            for name in missing_ids:
                print(f"   - {name}")
            print("\nPlease update the vector_store_id values in the script!")
            return False
        
        return True

    async def process_master(self, name: str, config: dict, batch_size: int = 100) -> bool:
        """Process a single chess master"""
        try:
            self.logger.info(f"Starting processing for {name}")
            print(f"\n🎯 Processing {name}...")
            
            # Initialize creator with current master config
            self.creator.master_configs[name] = config
            
            # Set up API client if not already done
            if not self.creator.client:
                config_file = Path("chess_db_config.json")
                if config_file.exists():
                    with open(config_file, 'r') as f:
                        api_config = json.load(f)
                        from openai import OpenAI
                        self.creator.client = OpenAI(api_key=api_config['api_key'])
                else:
                    print("❌ No API key configuration found!")
                    return False
            
            # Fetch games
            games_count = config.get("games_count", 25)
            games = await self.creator.fetch_with_responses_api(name, games_count)
            
            if not games:
                self.logger.error(f"❌ No games fetched for {name}")
                return False
            
            # Process positions
            positions = self.creator.fast_deduplicate_positions(games, name)
            
            if not positions:
                self.logger.error(f"❌ No valid positions for {name}")
                return False
            
            # Create batch files
            batch_files = self.creator.create_batch_json_files(positions, name, batch_size)
            
            # Upload to vector store
            vector_store_id = config.get("vector_store_id")
            if vector_store_id and vector_store_id.startswith("vs_"):
                success = await self.creator.upload_to_vector_store(positions, vector_store_id)
                
                if success:
                    self.logger.info(f"✅ {name}: {len(positions)} positions uploaded to {vector_store_id}")
                    print(f"✅ {name}: SUCCESS! {len(positions)} positions uploaded")
                    return True
                else:
                    self.logger.error(f"❌ {name}: Vector store upload failed")
                    print(f"❌ {name}: Vector store upload failed")
                    return False
            else:
                self.logger.warning(f"⚠️ {name}: No valid vector store ID, saved locally only")
                print(f"⚠️ {name}: Saved locally only (no vector store ID)")
                return True
                
        except Exception as e:
            self.logger.error(f"❌ {name}: Processing failed - {e}")
            print(f"❌ {name}: FAILED - {e}")
            return False

    async def run_batch_update(self, priorities: list = ["high"], batch_size: int = 100):
        """Run batch update for selected priority masters"""
        print("🚀 Chess Master Vector Store Batch Updater")
        print("=" * 50)
        
        # Validate configuration
        if not self.validate_vector_store_ids():
            return
        
        # Filter masters by priority
        selected_masters = {}
        for name, config in self.master_configs.items():
            if config.get("priority", "medium") in priorities:
                selected_masters[name] = config
        
        print(f"\n📋 Selected {len(selected_masters)} masters with priority {priorities}:")
        for name in selected_masters.keys():
            print(f"   - {name}")
        
        confirm = input(f"\nProceed with batch update? (y/N): ").strip().lower()
        if confirm != 'y':
            print("❌ Batch update cancelled")
            return
        
        # Process each master
        results = {}
        total_masters = len(selected_masters)
        
        for i, (name, config) in enumerate(selected_masters.items(), 1):
            print(f"\n📊 Progress: {i}/{total_masters}")
            success = await self.process_master(name, config, batch_size)
            results[name] = success
            
            # Add delay between masters to avoid rate limits
            if i < total_masters:
                print("⏱️ Waiting 10 seconds before next master...")
                await asyncio.sleep(10)
        
        # Summary
        print(f"\n🎉 Batch Update Complete!")
        print("=" * 50)
        
        successful = [name for name, success in results.items() if success]
        failed = [name for name, success in results.items() if not success]
        
        print(f"✅ Successful: {len(successful)}")
        for name in successful:
            print(f"   - {name}")
        
        if failed:
            print(f"\n❌ Failed: {len(failed)}")
            for name in failed:
                print(f"   - {name}")
        
        print(f"\n📊 Success Rate: {len(successful)}/{total_masters} ({len(successful)/total_masters*100:.1f}%)")

    def update_vector_store_ids(self, id_mapping: dict):
        """Update vector store IDs from a mapping"""
        for name, new_id in id_mapping.items():
            if name in self.master_configs:
                self.master_configs[name]["vector_store_id"] = new_id
                print(f"✅ Updated {name}: {new_id}")
            else:
                print(f"⚠️ Unknown master: {name}")

def main():
    """Run the batch updater"""
    updater = BatchVectorStoreUpdater()
    
    print("🎯 Batch Vector Store Updater")
    print("Choose update mode:")
    print("1. High priority masters only (Carlsen, Tal, Fischer, Kasparov, Karpov, Capablanca)")
    print("2. All masters")
    print("3. Update vector store IDs only")
    print("4. Custom selection")
    
    choice = input("Select mode (1-4): ").strip()
    
    if choice == "1":
        asyncio.run(updater.run_batch_update(["high"]))
    elif choice == "2":
        asyncio.run(updater.run_batch_update(["high", "medium", "low"]))
    elif choice == "3":
        print("\n📝 Update Vector Store IDs")
        print("Format: name=vector_store_id (one per line, empty line to finish)")
        id_mapping = {}
        while True:
            line = input("Enter mapping: ").strip()
            if not line:
                break
            if "=" in line:
                name, vs_id = line.split("=", 1)
                id_mapping[name.strip()] = vs_id.strip()
        
        if id_mapping:
            updater.update_vector_store_ids(id_mapping)
            # Save updated config
            with open("updated_master_configs.json", 'w') as f:
                json.dump(updater.master_configs, f, indent=2)
            print("💾 Updated configuration saved to updated_master_configs.json")
    elif choice == "4":
        print("\n📋 Available masters:")
        masters = list(updater.master_configs.keys())
        for i, name in enumerate(masters, 1):
            priority = updater.master_configs[name].get("priority", "medium")
            print(f"   {i}. {name} ({priority})")
        
        selected_indices = input("Enter numbers (comma-separated): ").strip()
        try:
            indices = [int(x.strip()) - 1 for x in selected_indices.split(",")]
            selected_names = [masters[i] for i in indices if 0 <= i < len(masters)]
            
            if selected_names:
                # Create temporary config with selected masters
                temp_config = {name: updater.master_configs[name] for name in selected_names}
                updater.master_configs = temp_config
                asyncio.run(updater.run_batch_update(["high", "medium", "low"]))
            else:
                print("❌ No valid masters selected")
        except ValueError:
            print("❌ Invalid input format")

if __name__ == "__main__":
    main()