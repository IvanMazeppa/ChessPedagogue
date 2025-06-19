import json
import hashlib
import re
from pathlib import Path
from typing import Dict, List, Set, Tuple
import shutil
from datetime import datetime

class ChessGameDeduplicator:
    """Advanced deduplication tool for chess position files"""
    
    def __init__(self):
        self.duplicates_found = 0
        self.positions_removed = 0
        self.game_signatures: Dict[str, int] = {}
        self.move_signatures: Dict[str, int] = {}
        
    def create_game_signature(self, position: Dict) -> str:
        """Create signature based on game metadata"""
        opponent = re.sub(r'[^\w\s]', '', str(position.get('opponent', ''))).strip().lower()
        year = str(position.get('year', ''))
        tournament = re.sub(r'[^\w\s]', '', str(position.get('tournament', ''))).strip().lower()
        
        signature_data = f"{opponent}|{year}|{tournament}"
        return hashlib.md5(signature_data.encode()).hexdigest()
    
    def create_position_signature(self, position: Dict) -> str:
        """Create signature based on FEN position"""
        fen = position.get('fen', '')
        if not fen:
            return ""
        
        # Use only board position part (ignore castling rights, en passant, etc. for some flexibility)
        fen_parts = fen.split(' ')
        if len(fen_parts) >= 2:
            board_position = f"{fen_parts[0]}|{fen_parts[1]}"  # board + active color
            return hashlib.md5(board_position.encode()).hexdigest()
        return ""
    
    def extract_moves_from_game(self, positions: List[Dict], game_sig: str) -> List[str]:
        """Extract move sequence from a game's positions"""
        game_positions = [p for p in positions if self.create_game_signature(p) == game_sig]
        game_positions.sort(key=lambda x: x.get('move_number', 0))
        
        moves = []
        for pos in game_positions:
            last_move = pos.get('last_move', '').strip()
            if last_move and last_move not in moves:
                moves.append(last_move)
        
        return moves[:20]  # First 20 moves for comparison
    
    def create_moves_signature(self, moves: List[str]) -> str:
        """Create signature from move sequence"""
        if len(moves) < 8:  # Need reasonable number of moves
            return ""
        
        moves_str = " ".join(moves)
        return hashlib.md5(moves_str.encode()).hexdigest()
    
    def analyze_file(self, filepath: Path) -> Dict:
        """Analyze a chess positions file for duplicates"""
        print(f"🔍 Analyzing: {filepath.name}")
        
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                data = json.load(f)
            
            # Handle both old and new formats
            if isinstance(data, dict) and 'positions' in data:
                positions = data['positions']
                metadata = data.get('metadata', {})
            else:
                positions = data
                metadata = {}
            
            print(f"   📊 Total positions: {len(positions)}")
            
            # Group by games and analyze
            games_analysis = self.group_positions_by_game(positions)
            
            # Find duplicate games
            duplicate_games = self.find_duplicate_games(games_analysis)
            
            # Find duplicate positions within games
            duplicate_positions = self.find_duplicate_positions(positions)
            
            return {
                'filepath': filepath,
                'total_positions': len(positions),
                'games_count': len(games_analysis),
                'duplicate_games': duplicate_games,
                'duplicate_positions': duplicate_positions,
                'positions': positions,
                'metadata': metadata
            }
            
        except Exception as e:
            print(f"   ❌ Error reading file: {e}")
            return None
    
    def group_positions_by_game(self, positions: List[Dict]) -> Dict[str, List[Dict]]:
        """Group positions by game signature"""
        games = {}
        
        for position in positions:
            game_sig = self.create_game_signature(position)
            if game_sig not in games:
                games[game_sig] = []
            games[game_sig].append(position)
        
        return games
    
    def find_duplicate_games(self, games_analysis: Dict[str, List[Dict]]) -> List[Tuple[str, str, int]]:
        """Find games that are duplicates based on move sequences"""
        duplicates = []
        processed_sigs = set()
        
        for game_sig, positions in games_analysis.items():
            if game_sig in processed_sigs:
                continue
                
            # Extract moves for this game
            moves = self.extract_moves_from_game(positions, game_sig)
            moves_sig = self.create_moves_signature(moves)
            
            if not moves_sig:
                continue
            
            # Check if we've seen these moves before
            if moves_sig in self.move_signatures:
                original_game_sig = self.move_signatures[moves_sig]
                duplicates.append((game_sig, original_game_sig, len(positions)))
                print(f"   🔄 Duplicate game found: {positions[0].get('opponent', 'Unknown')} ({positions[0].get('year', '')})")
            else:
                self.move_signatures[moves_sig] = game_sig
            
            processed_sigs.add(game_sig)
        
        return duplicates
    
    def find_duplicate_positions(self, positions: List[Dict]) -> List[Tuple[int, int]]:
        """Find individual positions that are exact duplicates"""
        duplicates = []
        seen_positions = {}
        
        for i, position in enumerate(positions):
            pos_sig = self.create_position_signature(position)
            if not pos_sig:
                continue
                
            if pos_sig in seen_positions:
                duplicates.append((i, seen_positions[pos_sig]))
            else:
                seen_positions[pos_sig] = i
        
        return duplicates
    
    def remove_duplicates(self, analysis: Dict) -> Dict:
        """Remove duplicates and return cleaned data"""
        positions = analysis['positions'].copy()
        removed_games = []
        removed_positions = []
        
        # Remove duplicate games (keep the first occurrence)
        for duplicate_game_sig, original_game_sig, pos_count in analysis['duplicate_games']:
            # Remove all positions from the duplicate game
            positions_to_remove = []
            for i, pos in enumerate(positions):
                if self.create_game_signature(pos) == duplicate_game_sig:
                    positions_to_remove.append(i)
            
            # Remove in reverse order to maintain indices
            for i in reversed(positions_to_remove):
                removed_pos = positions.pop(i)
                removed_games.append(removed_pos)
            
            print(f"   🗑️ Removed duplicate game: {removed_games[-1].get('opponent', 'Unknown')} ({pos_count} positions)")
        
        # Remove duplicate positions
        duplicate_indices = [dup[0] for dup in analysis['duplicate_positions']]
        duplicate_indices.sort(reverse=True)  # Remove from end to maintain indices
        
        for i in duplicate_indices:
            if i < len(positions):
                removed_pos = positions.pop(i)
                removed_positions.append(removed_pos)
        
        if removed_positions:
            print(f"   🗑️ Removed {len(removed_positions)} duplicate positions")
        
        self.duplicates_found += len(analysis['duplicate_games']) + len(analysis['duplicate_positions'])
        self.positions_removed += len(removed_games) + len(removed_positions)
        
        return {
            'positions': positions,
            'removed_games': removed_games,
            'removed_positions': removed_positions,
            'metadata': analysis['metadata']
        }
    
    def save_cleaned_file(self, filepath: Path, cleaned_data: Dict) -> Path:
        """Save cleaned data to file"""
        # Create backup first
        backup_path = filepath.with_suffix('.backup.json')
        shutil.copy2(filepath, backup_path)
        print(f"   💾 Backup saved: {backup_path.name}")
        
        # Update metadata
        metadata = cleaned_data['metadata']
        metadata.update({
            'cleaned_date': datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            'original_positions': metadata.get('total_positions', 0),
            'cleaned_positions': len(cleaned_data['positions']),
            'duplicates_removed': self.positions_removed
        })
        
        # Prepare output data
        output_data = {
            'metadata': metadata,
            'positions': cleaned_data['positions']
        }
        
        # Save cleaned file
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(output_data, f, ensure_ascii=False, indent=2)
        
        print(f"   ✅ Cleaned file saved: {len(cleaned_data['positions'])} positions")
        return filepath
    
    def process_file(self, filepath: Path, dry_run: bool = False) -> bool:
        """Process a single file for deduplication"""
        print(f"\n{'🔍' if dry_run else '🔧'} Processing: {filepath.name}")
        
        analysis = self.analyze_file(filepath)
        if not analysis:
            return False
        
        total_duplicates = len(analysis['duplicate_games']) + len(analysis['duplicate_positions'])
        
        if total_duplicates == 0:
            print("   ✅ No duplicates found - file is clean!")
            return True
        
        print(f"   📊 Found {len(analysis['duplicate_games'])} duplicate games")
        print(f"   📊 Found {len(analysis['duplicate_positions'])} duplicate positions")
        
        if dry_run:
            print("   🔍 DRY RUN - no changes made")
            return True
        
        # Clean the data
        cleaned_data = self.remove_duplicates(analysis)
        
        # Save cleaned file
        self.save_cleaned_file(filepath, cleaned_data)
        
        return True

def find_chess_files(directory: Path = None) -> List[Path]:
    """Find all chess position files"""
    search_dirs = []
    
    if directory:
        search_dirs.append(directory)
    else:
        # Search common locations
        search_dirs.extend([
            Path('.'),  # Current directory
            Path('chess_positions'),  # Output directory
            Path('assets'),  # Android assets
            Path('data')  # Data directory
        ])
    
    chess_files = []
    
    for search_dir in search_dirs:
        if search_dir.exists():
            # Look for files matching the pattern
            patterns = [
                '*_full_positions.json',
                '*_positions.json',
                'full_game_positions.json'
            ]
            
            for pattern in patterns:
                chess_files.extend(list(search_dir.glob(pattern)))
    
    return sorted(set(chess_files))  # Remove duplicates and sort

def main():
    """Main deduplication tool"""
    print("🧹 CHESS POSITION DEDUPLICATOR")
    print("=" * 40)
    print("🎯 Remove duplicate games and positions")
    print("=" * 40)
    
    # Find chess files
    chess_files = find_chess_files()
    
    if not chess_files:
        print("\n❌ No chess position files found!")
        print("💡 Place files in current directory or 'chess_positions' folder")
        print("📁 Expected format: *_full_positions.json")
        return
    
    print(f"\n📁 Found {len(chess_files)} chess position files:")
    for i, file in enumerate(chess_files, 1):
        print(f"   {i}. {file.name} ({file.stat().st_size / 1024:.1f} KB)")
    
    # Choose processing mode
    print(f"\n🔧 Processing Options:")
    print("   1. Analyze only (dry run)")
    print("   2. Clean all files")
    print("   3. Clean specific file")
    
    while True:
        try:
            choice = input("\nEnter choice (1-3): ").strip()
            if choice in ['1', '2', '3']:
                break
            print("❌ Please enter 1, 2, or 3")
        except KeyboardInterrupt:
            print("\n⏹️ Cancelled by user")
            return
    
    deduplicator = ChessGameDeduplicator()
    
    if choice == '1':  # Dry run
        print("\n🔍 ANALYSIS MODE - No files will be modified")
        for filepath in chess_files:
            deduplicator.process_file(filepath, dry_run=True)
    
    elif choice == '2':  # Clean all
        print("\n🔧 CLEANING ALL FILES")
        for filepath in chess_files:
            deduplicator.process_file(filepath, dry_run=False)
    
    elif choice == '3':  # Clean specific
        print("\nSelect file to clean:")
        for i, file in enumerate(chess_files, 1):
            print(f"   {i}. {file.name}")
        
        while True:
            try:
                file_choice = int(input(f"\nEnter file number (1-{len(chess_files)}): ").strip())
                if 1 <= file_choice <= len(chess_files):
                    selected_file = chess_files[file_choice - 1]
                    break
                print(f"❌ Please enter a number between 1 and {len(chess_files)}")
            except (ValueError, KeyboardInterrupt):
                print("\n⏹️ Cancelled by user")
                return
        
        print(f"\n🔧 CLEANING: {selected_file.name}")
        deduplicator.process_file(selected_file, dry_run=False)
    
    # Show summary
    if deduplicator.duplicates_found > 0:
        print(f"\n📊 SUMMARY:")
        print(f"   Duplicate issues found: {deduplicator.duplicates_found}")
        print(f"   Positions removed: {deduplicator.positions_removed}")
        print(f"   Files processed: {len(chess_files)}")
        
        if choice != '1':
            print(f"\n✅ Deduplication complete!")
            print(f"💾 Original files backed up with .backup.json extension")
    else:
        print(f"\n✅ All files are clean - no duplicates found!")

if __name__ == "__main__":
    main()