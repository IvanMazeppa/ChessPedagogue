import asyncio
import json
from pathlib import Path
from chunked_fen_db_creator_v9_fixed import FixedResponsesAPIChessCreator
import logging
from datetime import datetime

class BatchVectorStoreUpdater:
    """Batch processor for updating all chess master vector stores with COMPLETE ID mapping"""
    
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
        """Complete chess master configurations with ALL your vector store IDs and optimized game counts"""
        return {
            "Magnus Carlsen": {
                "assistant_id": "asst_TTzxbfvJQz3e80FetQblJ0Gl",
                "vector_store_id": "vs_68365028eb988191b09d8d50e6f11b5d",
                "current_size_kb": 123,
                "model": "ft:gpt-4o-mini-2024-07-18:personal:magnus-carlsen-chess-master:AIHj5PaY",
                "style_keywords": "endgame grinding, positional mastery, universal style, calculation depth",
                "games_count": 25,  # Optimal size, slight reduction from 123KB
                "priority": "high",
                "recommendation": "Reduce slightly for optimal performance"
            },
            "Mikhail Tal": {
                "assistant_id": "asst_VKHRwLqOD8dqvQUmRq2vf8OL", 
                "vector_store_id": "vs_682f419a57288191aa3cd922b27acb5f",
                "current_size_kb": 184,
                "model": "ft:gpt-4o-mini-2024-07-18:personal:mikhail-tal-chess-master:AHkZonl6",
                "style_keywords": "sacrificial brilliance, tactical genius, intuitive play, artistic combinations",
                "games_count": 20,  # REDUCE from 184KB - too large for optimal performance
                "priority": "high",
                "recommendation": "URGENT: Reduce size for better performance"
            },
            "Bobby Fischer": {
                "assistant_id": "asst_s8LrE2n5qWCBF6ZLMeDNLmGd",
                "vector_store_id": "vs_6834a715ef788191bd9ef4caa5676436",
                "current_size_kb": 47,
                "model": "ft:gpt-4o-mini-2024-07-18:personal:bobby-fischer-chess-master:AIHjN5qJ",
                "style_keywords": "precise calculation, opening mastery, systematic approach, clarity",
                "games_count": 30,  # Increase from 47KB - needs more diversity
                "priority": "high",
                "recommendation": "Increase for better coverage"
            },
            "Garry Kasparov": {
                "vector_store_id": "vs_684f1232c8008191977caa8b9fd4e7fe",
                "current_size_kb": 58,
                "style_keywords": "dynamic play, computer analysis, deep preparation, fighting spirit",
                "games_count": 35,  # Increase from 58KB
                "priority": "high",
                "recommendation": "Increase for better coverage"
            },
            "Anatoly Karpov": {
                "assistant_id": "asst_XtOb3YIdrKK7IfE075npf8GA",
                "vector_store_id": "vs_6851b7017db88191a2b617ba8b9b49f5",
                "current_size_kb": 0,
                "style_keywords": "positional mastery, strategic depth, endgame technique, patient maneuvering, institutional chess",
                "games_count": 30,
                "priority": "high",
                "recommendation": "NEW STORE - Create initial high-quality dataset"
            },
            "Vladimir Kramnik": {
                "vector_store_id": "vs_6850574a7ca881918448771b3981a9be",
                "current_size_kb": 101,
                "style_keywords": "solid positional play, Berlin Defense, endgame excellence, computer preparation",
                "games_count": 25,  # Optimal size
                "priority": "medium",
                "recommendation": "Perfect size - minor update for quality"
            },
            "Jose Raul Capablanca": {
                "vector_store_id": "vs_68411f200f9c81918f4a7d95f81e178f",
                "current_size_kb": 70,
                "style_keywords": "natural talent, simplicity, endgame mastery, effortless technique",
                "games_count": 30,  # Slight increase
                "priority": "high",
                "recommendation": "Increase slightly"
            },
            "Emanuel Lasker": {
                "vector_store_id": "vs_685067a562cc81919538f59d527281cd",
                "current_size_kb": 92,
                "style_keywords": "psychological warfare, fighting spirit, practical play, late-career brilliance",
                "games_count": 25,  # Optimal size
                "priority": "medium",
                "recommendation": "Good size - quality upgrade only"
            },
            "Paul Morphy": {
                "vector_store_id": "vs_68506c1881ac8191b3804458b3762310",
                "current_size_kb": 31,
                "style_keywords": "romantic chess, rapid development, tactical brilliance, natural genius",
                "games_count": 35,  # INCREASE from 31KB - too small
                "priority": "medium",
                "recommendation": "Increase significantly"
            },
            "Viswanathan Anand": {
                "vector_store_id": "vs_683a6d79f3f881918134880655179275",
                "current_size_kb": 62,
                "style_keywords": "speed chess, versatile style, rapid calculation, world champion mentality",
                "games_count": 30,
                "priority": "medium",
                "recommendation": "Increase for better coverage"
            },
            "Mikhail Botvinnik": {
                "vector_store_id": "vs_682a4788c5508191949808a00cb6c4b7",
                "current_size_kb": 42,
                "style_keywords": "scientific approach, systematic preparation, strong technique, soviet school",
                "games_count": 35,  # INCREASE from 42KB - too small
                "priority": "low",
                "recommendation": "Increase significantly"
            },
            "Alexander Alekhine": {
                "vector_store_id": "vs_683e1b8b55d08191accfeebc2d4900db",
                "current_size_kb": 58,
                "style_keywords": "combinatorial genius, attacking brilliance, deep calculation, tactical artistry",
                "games_count": 30,
                "priority": "medium",
                "recommendation": "Increase for better coverage"
            },
            "Hikaru Nakamura": {
                "vector_store_id": "vs_684f2b03d184819196303badfcb7afd9",
                "current_size_kb": 124,
                "style_keywords": "speed chess mastery, tactical alertness, modern streaming, online dominance",
                "games_count": 25,  # Reduce slightly from 124KB
                "priority": "medium",
                "recommendation": "Reduce slightly for optimal performance"
            },
            "Gukesh Dommaraju": {
                "vector_store_id": "vs_68507d372234819180dab47bb73ddc88",
                "current_size_kb": 118,
                "style_keywords": "youthful brilliance, modern preparation, engine-assisted analysis, rising star",
                "games_count": 25,  # Reduce slightly from 118KB
                "priority": "low",
                "recommendation": "Reduce slightly"
            },
            "Nigel Short": {
                "vector_store_id": "vs_68507c1fe2e08191be5b61bab3b47eef",
                "current_size_kb": 67,
                "style_keywords": "fighting chess, tactical sharpness, English pragmatism, counterattacking",
                "games_count": 30,
                "priority": "low",
                "recommendation": "Slight increase"
            },
            "Tigran Petrosian": {
                "vector_store_id": "vs_684f1d155024819191789335759adcfb",
                "current_size_kb": 78,
                "style_keywords": "iron logic, prophylactic thinking, defensive mastery, positional understanding",
                "games_count": 30,
                "priority": "medium",
                "recommendation": "Good size - quality upgrade"
            },
            "Aron Nimzowitsch": {
                "vector_store_id": "vs_684f17482a3481918151b75dc2f460be",
                "current_size_kb": 106,
                "style_keywords": "hypermodern pioneer, blockade theory, positional innovation, strategic creativity",
                "games_count": 25,  # Good size
                "priority": "low",
                "recommendation": "Good size - quality upgrade only"
            }
        }

    def get_performance_analysis(self):
        """Analyze current vector store performance and provide recommendations"""
        print("\n📊 VECTOR STORE PERFORMANCE ANALYSIS")
        print("=" * 60)
        
        urgent_updates = []
        recommended_updates = []
        optimal_stores = []
        
        for name, config in self.master_configs.items():
            size_kb = config.get("current_size_kb", 0)
            recommendation = config.get("recommendation", "")
            
            if "URGENT" in recommendation or size_kb > 150:
                urgent_updates.append((name, size_kb, recommendation))
            elif "Increase" in recommendation or size_kb < 50:
                recommended_updates.append((name, size_kb, recommendation))
            else:
                optimal_stores.append((name, size_kb, recommendation))
        
        print(f"🚨 URGENT UPDATES ({len(urgent_updates)}):")
        for name, size, rec in urgent_updates:
            print(f"   - {name}: {size}KB - {rec}")
        
        print(f"\n📈 RECOMMENDED UPDATES ({len(recommended_updates)}):")
        for name, size, rec in recommended_updates:
            print(f"   - {name}: {size}KB - {rec}")
        
        print(f"\n✅ OPTIMAL SIZE ({len(optimal_stores)}):")
        for name, size, rec in optimal_stores:
            print(f"   - {name}: {size}KB - {rec}")
        
        return urgent_updates, recommended_updates, optimal_stores

    def validate_vector_store_ids(self) -> bool:
        """Check if all vector store IDs are provided"""
        missing_ids = []
        for name, config in self.master_configs.items():
            vs_id = config.get("vector_store_id", "")
            if not vs_id.startswith("vs_") or "MISSING" in vs_id:
                missing_ids.append(name)
        
        if missing_ids:
            print("❌ Missing vector store IDs for:")
            for name in missing_ids:
                print(f"   - {name}")
            return False
        
        return True

    async def process_master(self, name: str, config: dict, batch_size: int = 100) -> bool:
        """Process a single chess master with optimized settings"""
        try:
            self.logger.info(f"Starting processing for {name}")
            current_size = config.get("current_size_kb", 0)
            target_games = config.get("games_count", 25)
            print(f"\n🎯 Processing {name}...")
            print(f"   Current: {current_size}KB → Target: {target_games} games")
            
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
            
            # Fetch games with optimized count
            games = await self.creator.fetch_with_responses_api(name, target_games)
            
            if not games:
                self.logger.error(f"❌ No games fetched for {name}")
                return False
            
            # Process positions
            positions = self.creator.fast_deduplicate_positions(games, name)
            
            if not positions:
                self.logger.error(f"❌ No valid positions for {name}")
                return False
            
            # Estimate new size
            estimated_kb = len(positions) * 3  # Rough estimate: 3KB per position
            
            # Create batch files
            batch_files = self.creator.create_batch_json_files(positions, name, batch_size)
            
            # Upload to vector store
            vector_store_id = config.get("vector_store_id")
            if vector_store_id and vector_store_id.startswith("vs_"):
                success = await self.creator.upload_to_vector_store(positions, vector_store_id)
                
                if success:
                    self.logger.info(f"✅ {name}: {len(positions)} positions uploaded to {vector_store_id}")
                    print(f"✅ {name}: SUCCESS! {len(positions)} positions (~{estimated_kb}KB)")
                    return True
                else:
                    self.logger.error(f"❌ {name}: Vector store upload failed")
                    print(f"❌ {name}: Vector store upload failed")
                    return False
            else:
                self.logger.warning(f"⚠️ {name}: No valid vector store ID")
                print(f"⚠️ {name}: No valid vector store ID")
                return False
                
        except Exception as e:
            self.logger.error(f"❌ {name}: Processing failed - {e}")
            print(f"❌ {name}: FAILED - {e}")
            return False

    async def run_batch_update(self, update_mode: str = "urgent", batch_size: int = 100):
        """Run batch update with performance optimization"""
        print("🚀 OPTIMIZED Chess Master Vector Store Updater")
        print("=" * 60)
        
        # Performance analysis
        urgent, recommended, optimal = self.get_performance_analysis()
        
        # Select masters based on mode
        if update_mode == "urgent":
            selected_masters = {name: self.master_configs[name] for name, _, _ in urgent}
            print(f"\n🚨 URGENT MODE: Updating {len(selected_masters)} critical stores")
        elif update_mode == "recommended":
            all_updates = urgent + recommended
            selected_masters = {name: self.master_configs[name] for name, _, _ in all_updates}
            print(f"\n📈 RECOMMENDED MODE: Updating {len(selected_masters)} stores")
        elif update_mode == "all":
            selected_masters = self.master_configs
            print(f"\n🔄 FULL MODE: Updating all {len(selected_masters)} stores")
        elif update_mode == "quality_only":
            # Only update stores that need quality improvements, not size changes
            quality_updates = [name for name, _, rec in optimal if "quality" in rec.lower()]
            selected_masters = {name: self.master_configs[name] for name in quality_updates}
            print(f"\n✨ QUALITY MODE: Updating {len(selected_masters)} stores for quality only")
        else:
            print("❌ Invalid update mode")
            return
        
        # Validate IDs
        if not self.validate_vector_store_ids():
            print("\n⚠️ Some vector store IDs are missing. Continue anyway? (y/N)")
            if input().strip().lower() != 'y':
                return
        
        if not selected_masters:
            print("❌ No masters selected for update")
            return
        
        print(f"\n📋 Selected masters:")
        for name, config in selected_masters.items():
            current_kb = config.get("current_size_kb", 0)
            target_games = config.get("games_count", 25)
            priority = config.get("priority", "medium")
            print(f"   - {name}: {current_kb}KB → {target_games} games ({priority})")
        
        # Estimate costs
        total_games = sum(config.get("games_count", 25) for config in selected_masters.values())
        estimated_cost = total_games * 0.10  # Rough estimate: $0.10 per game generation
        
        print(f"\n💰 Estimated cost: ~${estimated_cost:.2f} (assuming $0.10/game)")
        print(f"⏱️ Estimated time: ~{len(selected_masters) * 5} minutes")
        
        confirm = input(f"\nProceed with {update_mode} update? (y/N): ").strip().lower()
        if confirm != 'y':
            print("❌ Update cancelled")
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
                print("⏱️ Waiting 15 seconds before next master...")
                await asyncio.sleep(15)
        
        # Summary
        print(f"\n🎉 Batch Update Complete!")
        print("=" * 60)
        
        successful = [name for name, success in results.items() if success]
        failed = [name for name, success in results.items() if not success]
        
        print(f"✅ Successful: {len(successful)}")
        for name in successful:
            print(f"   - {name}")
        
        if failed:
            print(f"\n❌ Failed: {len(failed)}")
            for name in failed:
                print(f"   - {name}")
        
        success_rate = len(successful)/total_masters*100 if total_masters > 0 else 0
        print(f"\n📊 Success Rate: {len(successful)}/{total_masters} ({success_rate:.1f}%)")

def main():
    """Run the optimized batch updater"""
    updater = BatchVectorStoreUpdater()
    
    print("🎯 OPTIMIZED Vector Store Updater")
    print("Choose update strategy:")
    print("1. 🚨 URGENT ONLY - Fix performance issues (Tal: 184KB)")
    print("2. 📈 RECOMMENDED - All size optimizations")
    print("3. ✨ QUALITY ONLY - Keep sizes, improve quality")
    print("4. 🔄 ALL STORES - Complete rebuild")
    print("5. 📊 ANALYSIS ONLY - Show recommendations")
    
    choice = input("Select option (1-5): ").strip()
    
    if choice == "1":
        asyncio.run(updater.run_batch_update("urgent"))
    elif choice == "2":
        asyncio.run(updater.run_batch_update("recommended"))
    elif choice == "3":
        asyncio.run(updater.run_batch_update("quality_only"))
    elif choice == "4":
        asyncio.run(updater.run_batch_update("all"))
    elif choice == "5":
        updater.get_performance_analysis()
        print("\n💡 RECOMMENDATIONS:")
        print("- Start with URGENT updates (option 1)")
        print("- Then do QUALITY updates (option 3) for your fine-tuned models")
        print("- Consider RECOMMENDED updates (option 2) for complete optimization")
    else:
        print("❌ Invalid choice")

if __name__ == "__main__":
    main()