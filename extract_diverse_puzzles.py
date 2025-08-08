#!/usr/bin/env python3
"""
🧩 Lichess Puzzle Database Extractor
Extracts 5,000 diverse tactical puzzles from the full Lichess database.

Strategy:
- Balanced rating distribution (600-2800 range)
- Diverse tactical themes (mate, fork, pin, skewer, etc.)
- High-quality puzzles (popularity > 60, plays > 100)
- No duplicate positions or overly similar puzzles
"""

import csv
import random
import sys
from collections import defaultdict, Counter
from typing import Dict, List, Set

class PuzzleExtractor:
    def __init__(self, input_file: str, output_file: str):
        self.input_file = input_file
        self.output_file = output_file
        self.target_count = 5000
        
        # Balanced distribution targets
        self.rating_targets = {
            (600, 1000): 500,    # Beginner
            (1000, 1200): 600,   # Easy
            (1200, 1400): 800,   # Medium
            (1400, 1600): 800,   # Hard
            (1600, 1800): 600,   # Expert
            (1800, 2000): 500,   # Master
            (2000, 2200): 300,   # Advanced
            (2200, 2800): 200,   # Grandmaster
        }
        
        # Priority themes for tactical training
        self.priority_themes = {
            'mate', 'mateIn1', 'mateIn2', 'mateIn3', 'mateIn4',
            'fork', 'royalFork', 'pin', 'skewer', 'discoveredAttack',
            'deflection', 'decoy', 'attraction', 'clearance', 'interference',
            'sacrifice', 'greekGift', 'smothered', 'backRank', 'zugzwang',
            'promotion', 'underPromotion', 'endgame', 'advantage'
        }
        
        # Quality filters
        self.min_popularity = 60
        self.min_plays = 100
        
        # State tracking
        self.selected_puzzles = []
        self.rating_counts = defaultdict(int)
        self.theme_counts = defaultdict(int)
        self.used_fens = set()
        
    def extract_puzzles(self):
        """Main extraction process"""
        print("🚀 Starting puzzle extraction from Lichess database...")
        print(f"📊 Target: {self.target_count} diverse puzzles")
        
        # Pass 1: Collect candidates by rating ranges
        candidates = self._collect_candidates()
        
        # Pass 2: Select balanced distribution
        self._select_balanced_puzzles(candidates)
        
        # Pass 3: Write to output file
        self._write_output()
        
        # Report statistics
        self._print_statistics()
        
    def _collect_candidates(self) -> Dict[tuple, List[dict]]:
        """Collect candidate puzzles organized by rating ranges"""
        print("📈 Collecting candidates from database...")
        
        candidates = {rating_range: [] for rating_range in self.rating_targets.keys()}
        
        with open(self.input_file, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            
            processed = 0
            for row in reader:
                processed += 1
                if processed % 100000 == 0:
                    print(f"   Processed {processed:,} puzzles...")
                
                # Quality filters
                try:
                    rating = int(row['Rating'])
                    popularity = int(row['Popularity'])
                    plays = int(row['NbPlays'])
                    
                    if popularity < self.min_popularity or plays < self.min_plays:
                        continue
                        
                    # Check for priority themes
                    themes = set(row['Themes'].split())
                    if not themes.intersection(self.priority_themes):
                        continue
                        
                    # Find appropriate rating range
                    rating_range = self._get_rating_range(rating)
                    if rating_range and len(candidates[rating_range]) < self.rating_targets[rating_range] * 5:  # Collect 5x target for selection
                        candidates[rating_range].append({
                            'PuzzleId': row['PuzzleId'],
                            'FEN': row['FEN'],
                            'Moves': row['Moves'],
                            'Rating': rating,
                            'RatingDeviation': int(row['RatingDeviation']),
                            'Popularity': popularity,
                            'NbPlays': plays,
                            'Themes': row['Themes'],
                            'GameUrl': row['GameUrl'],
                            'OpeningTags': row['OpeningTags'],
                            'themes_set': themes
                        })
                        
                except (ValueError, KeyError):
                    continue
        
        # Report collection results
        for rating_range, puzzles in candidates.items():
            print(f"   {rating_range[0]}-{rating_range[1]}: {len(puzzles):,} candidates")
            
        return candidates
    
    def _select_balanced_puzzles(self, candidates: Dict[tuple, List[dict]]):
        """Select puzzles with balanced distribution"""
        print("🎯 Selecting balanced puzzle distribution...")
        
        for rating_range, target_count in self.rating_targets.items():
            range_candidates = candidates[rating_range]
            
            if len(range_candidates) < target_count:
                print(f"   ⚠️ Only {len(range_candidates)} candidates for {rating_range}, using all")
                selected = range_candidates
            else:
                # Sort by quality score and select best ones
                range_candidates.sort(key=self._quality_score, reverse=True)
                selected = self._diverse_selection(range_candidates, target_count)
            
            self.selected_puzzles.extend(selected)
            self.rating_counts[rating_range] = len(selected)
            
            # Track theme distribution
            for puzzle in selected:
                for theme in puzzle['themes_set']:
                    self.theme_counts[theme] += 1
                    
            print(f"   ✅ {rating_range[0]}-{rating_range[1]}: {len(selected)} puzzles selected")
    
    def _diverse_selection(self, candidates: List[dict], target: int) -> List[dict]:
        """Select diverse puzzles avoiding duplicates and similar positions"""
        selected = []
        
        # Group by primary theme for diversity
        theme_groups = defaultdict(list)
        for puzzle in candidates:
            primary_theme = self._get_primary_theme(puzzle['themes_set'])
            theme_groups[primary_theme].append(puzzle)
        
        # Round-robin selection from theme groups
        theme_lists = list(theme_groups.values())
        random.shuffle(theme_lists)  # Randomize theme order
        
        while len(selected) < target and any(theme_lists):
            for theme_list in theme_lists[:]:
                if not theme_list:
                    theme_lists.remove(theme_list)
                    continue
                    
                puzzle = theme_list.pop(0)
                
                # Check for FEN duplicates (approximate)
                fen_key = self._normalize_fen(puzzle['FEN'])
                if fen_key in self.used_fens:
                    continue
                    
                selected.append(puzzle)
                self.used_fens.add(fen_key)
                
                if len(selected) >= target:
                    break
        
        return selected
    
    def _quality_score(self, puzzle: dict) -> float:
        """Calculate quality score for puzzle ranking"""
        popularity = puzzle['Popularity']
        plays = min(puzzle['NbPlays'], 10000)  # Cap plays for scoring
        rating_dev = puzzle['RatingDeviation']
        
        # Prefer high popularity, good play count, low rating deviation
        score = (popularity / 100.0) + (plays / 10000.0) + (1.0 - rating_dev / 100.0)
        
        # Bonus for priority themes
        priority_bonus = len(puzzle['themes_set'].intersection(self.priority_themes)) * 0.1
        
        return score + priority_bonus
    
    def _get_rating_range(self, rating: int) -> tuple:
        """Find which rating range a puzzle belongs to"""
        for (min_rating, max_rating) in self.rating_targets.keys():
            if min_rating <= rating < max_rating:
                return (min_rating, max_rating)
        return None
    
    def _get_primary_theme(self, themes: Set[str]) -> str:
        """Get primary theme for diversity grouping"""
        priority_order = ['mate', 'fork', 'pin', 'skewer', 'sacrifice', 'endgame']
        
        for theme in priority_order:
            if any(t.startswith(theme) for t in themes):
                return theme
                
        # Return first theme if no priority match
        return list(themes)[0] if themes else 'tactical'
    
    def _normalize_fen(self, fen: str) -> str:
        """Normalize FEN for duplicate detection (keep position, ignore move counts)"""
        parts = fen.split()
        if len(parts) >= 4:
            return ' '.join(parts[:4])  # Keep position, active color, castling, en passant
        return fen
    
    def _write_output(self):
        """Write selected puzzles to output CSV"""
        print(f"💾 Writing {len(self.selected_puzzles)} puzzles to {self.output_file}")
        
        # Shuffle for random order
        random.shuffle(self.selected_puzzles)
        
        with open(self.output_file, 'w', newline='', encoding='utf-8') as f:
            fieldnames = ['PuzzleId', 'FEN', 'Moves', 'Rating', 'RatingDeviation', 
                         'Popularity', 'NbPlays', 'Themes', 'GameUrl', 'OpeningTags']
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            
            writer.writeheader()
            for puzzle in self.selected_puzzles:
                # Remove the themes_set field before writing
                output_row = {k: v for k, v in puzzle.items() if k != 'themes_set'}
                writer.writerow(output_row)
    
    def _print_statistics(self):
        """Print extraction statistics"""
        print("\n📊 Extraction Statistics:")
        print(f"   Total puzzles extracted: {len(self.selected_puzzles):,}")
        
        print("\n🎯 Rating Distribution:")
        for rating_range, count in sorted(self.rating_counts.items()):
            percentage = (count / len(self.selected_puzzles)) * 100
            print(f"   {rating_range[0]:4d}-{rating_range[1]:4d}: {count:4d} puzzles ({percentage:5.1f}%)")
        
        print("\n🏷️ Top Themes:")
        for theme, count in Counter(self.theme_counts).most_common(15):
            percentage = (count / len(self.selected_puzzles)) * 100
            print(f"   {theme:20s}: {count:4d} puzzles ({percentage:5.1f}%)")
        
        print(f"\n✅ Successfully extracted diverse puzzle set!")
        print(f"📁 Output file: {self.output_file}")

def main():
    input_file = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/puzzle_database/lichess_db_puzzle.csv"
    output_file = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/app/src/main/assets/lichess_puzzles_5k.csv"
    
    extractor = PuzzleExtractor(input_file, output_file)
    extractor.extract_puzzles()

if __name__ == "__main__":
    main()