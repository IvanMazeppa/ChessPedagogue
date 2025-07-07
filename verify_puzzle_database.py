#!/usr/bin/env python3
"""
🔍 Puzzle Database Verification Script
Analyzes the extracted puzzle database for quality and distribution.
"""

import csv
from collections import Counter, defaultdict

def analyze_puzzle_database(filename):
    """Analyze the puzzle database for completeness and quality"""
    print(f"🔍 Analyzing puzzle database: {filename}")
    
    puzzles = []
    rating_distribution = defaultdict(int)
    theme_distribution = Counter()
    quality_metrics = {
        'total_puzzles': 0,
        'high_quality': 0,  # popularity > 80
        'very_popular': 0,  # plays > 1000
        'mate_puzzles': 0,
        'tactical_variety': set()
    }
    
    with open(filename, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        
        for row in reader:
            puzzles.append(row)
            quality_metrics['total_puzzles'] += 1
            
            # Rating distribution (100-point buckets)
            rating = int(row['Rating'])
            rating_bucket = (rating // 100) * 100
            rating_distribution[rating_bucket] += 1
            
            # Theme analysis
            themes = row['Themes'].split()
            for theme in themes:
                theme_distribution[theme] += 1
                quality_metrics['tactical_variety'].add(theme)
            
            # Quality metrics
            popularity = int(row['Popularity'])
            plays = int(row['NbPlays'])
            
            if popularity > 80:
                quality_metrics['high_quality'] += 1
            if plays > 1000:
                quality_metrics['very_popular'] += 1
            if any('mate' in theme.lower() for theme in themes):
                quality_metrics['mate_puzzles'] += 1
    
    # Print analysis results
    print(f"\n📊 Database Overview:")
    print(f"   Total puzzles: {quality_metrics['total_puzzles']:,}")
    print(f"   High quality (>80 popularity): {quality_metrics['high_quality']:,} ({quality_metrics['high_quality']/quality_metrics['total_puzzles']*100:.1f}%)")
    print(f"   Very popular (>1000 plays): {quality_metrics['very_popular']:,} ({quality_metrics['very_popular']/quality_metrics['total_puzzles']*100:.1f}%)")
    print(f"   Mate puzzles: {quality_metrics['mate_puzzles']:,} ({quality_metrics['mate_puzzles']/quality_metrics['total_puzzles']*100:.1f}%)")
    print(f"   Tactical variety: {len(quality_metrics['tactical_variety'])} different themes")
    
    print(f"\n📈 Rating Distribution:")
    for rating_bucket in sorted(rating_distribution.keys()):
        count = rating_distribution[rating_bucket]
        percentage = (count / quality_metrics['total_puzzles']) * 100
        difficulty = get_difficulty_name(rating_bucket)
        print(f"   {rating_bucket:4d}-{rating_bucket+99:4d} ({difficulty:12s}): {count:4d} puzzles ({percentage:5.1f}%)")
    
    print(f"\n🏷️ Top 20 Tactical Themes:")
    for theme, count in theme_distribution.most_common(20):
        percentage = (count / quality_metrics['total_puzzles']) * 100
        print(f"   {theme:20s}: {count:4d} puzzles ({percentage:5.1f}%)")
    
    # Sample some puzzles for verification
    print(f"\n🎯 Sample Puzzles (Random Selection):")
    import random
    sample_puzzles = random.sample(puzzles, min(5, len(puzzles)))
    
    for i, puzzle in enumerate(sample_puzzles, 1):
        print(f"   {i}. {puzzle['PuzzleId']} (Rating: {puzzle['Rating']})")
        print(f"      Themes: {puzzle['Themes']}")
        print(f"      FEN: {puzzle['FEN']}")
        print(f"      Moves: {puzzle['Moves']}")
        print(f"      Quality: {puzzle['Popularity']}% popularity, {puzzle['NbPlays']} plays")
        print()
    
    # Validation checks
    print(f"✅ Validation Results:")
    issues = []
    
    if quality_metrics['total_puzzles'] < 4000:
        issues.append(f"Low puzzle count: {quality_metrics['total_puzzles']} (expected 4000+)")
    
    if quality_metrics['high_quality'] / quality_metrics['total_puzzles'] < 0.7:
        issues.append(f"Low quality ratio: {quality_metrics['high_quality']/quality_metrics['total_puzzles']*100:.1f}% (expected 70%+)")
    
    if len(quality_metrics['tactical_variety']) < 20:
        issues.append(f"Low tactical variety: {len(quality_metrics['tactical_variety'])} themes (expected 20+)")
    
    if not issues:
        print("   🎉 All validation checks passed!")
        print("   📚 Database is ready for production use")
    else:
        print("   ⚠️ Issues found:")
        for issue in issues:
            print(f"      - {issue}")
    
    return puzzles, quality_metrics

def get_difficulty_name(rating):
    """Get difficulty name for rating range"""
    if rating < 1000:
        return "Beginner"
    elif rating < 1200:
        return "Easy"
    elif rating < 1400:
        return "Medium"
    elif rating < 1600:
        return "Hard"
    elif rating < 1800:
        return "Expert"
    elif rating < 2000:
        return "Master"
    elif rating < 2200:
        return "Advanced"
    else:
        return "Grandmaster"

def main():
    puzzle_file = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/app/src/main/assets/lichess_puzzles_5k.csv"
    
    try:
        puzzles, metrics = analyze_puzzle_database(puzzle_file)
        print(f"\n🚀 Analysis complete! Database contains {metrics['total_puzzles']:,} professional-quality puzzles.")
    except FileNotFoundError:
        print(f"❌ Error: Could not find puzzle file at {puzzle_file}")
    except Exception as e:
        print(f"❌ Error analyzing database: {e}")

if __name__ == "__main__":
    main()