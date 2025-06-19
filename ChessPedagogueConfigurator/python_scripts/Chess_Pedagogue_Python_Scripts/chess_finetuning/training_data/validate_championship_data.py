#!/usr/bin/env python3
"""
Capablanca Championship Training Data Validator
==============================================

This script validates and analyzes the enhanced Capablanca training data
to ensure it meets the highest quality standards for creating the best
chess AI personality, superior to Fischer, Tal, and Alekhine.

Author: AI Enhancement Team
Date: June 2025
"""

import json
import re
from collections import defaultdict, Counter
from typing import Dict, List, Tuple, Any
import statistics

def load_jsonl(file_path: str) -> List[Dict]:
    """Load JSONL training data."""
    data = []
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                if line:
                    try:
                        entry = json.loads(line)
                        data.append(entry)
                    except json.JSONDecodeError as e:
                        print(f"⚠️  JSON error on line {line_num}: {e}")
        print(f"✅ Loaded {len(data)} entries from {file_path}")
        return data
    except FileNotFoundError:
        print(f"❌ File not found: {file_path}")
        return []

def analyze_human_like_features(text: str) -> Dict[str, int]:
    """Analyze text for human-like conversational features."""
    features = {
        'uncertainty_markers': len(re.findall(r'\b(well|I think|perhaps|maybe|actually|probably|seems|appears|might|could)\b', text, re.IGNORECASE)),
        'emotional_expressions': len(re.findall(r'\*(.*?)\*|sighs|chuckles|laughs|grins|winces|pauses|adjusts|touches', text, re.IGNORECASE)),
        'personal_pronouns': len(re.findall(r'\b(I|me|my|myself)\b', text, re.IGNORECASE)),
        'conversational_fillers': len(re.findall(r'\b(you know|well|uh|actually|anyway|so|hmm)\b', text, re.IGNORECASE)),
        'self_corrections': len(re.findall(r'\b(wait|no|actually|I mean|that is|rather)\b', text, re.IGNORECASE)),
        'ellipses_pauses': len(re.findall(r'\.\.\.', text)),
        'question_markers': len(re.findall(r'\?', text)),
        'exclamations': len(re.findall(r'!', text)),
        'sensory_details': len(re.findall(r'\b(feel|see|hear|smell|taste|touch|warm|cold|hot|tired|excited)\b', text, re.IGNORECASE)),
        'memory_references': len(re.findall(r'\b(remember|recall|think back|reminds me|memory|forgot|unforgettable)\b', text, re.IGNORECASE))
    }
    return features

def analyze_chess_accuracy(text: str) -> Dict[str, int]:
    """Analyze chess content for technical accuracy indicators."""
    features = {
        'chess_notation': len(re.findall(r'\b[KQRNB]?[a-h]?[1-8]?x?[a-h][1-8][=]?[QRNB]?[+#]?\b', text)),
        'positional_terms': len(re.findall(r'\b(center|development|initiative|space|weakness|strength|outpost|fianchetto)\b', text, re.IGNORECASE)),
        'tactical_terms': len(re.findall(r'\b(pin|fork|skewer|discovery|sacrifice|combination|tactic|deflection)\b', text, re.IGNORECASE)),
        'endgame_terms': len(re.findall(r'\b(opposition|zugzwang|passer|breakthrough|technique|lucena|philidor)\b', text, re.IGNORECASE)),
        'opening_terms': len(re.findall(r'\b(gambit|variation|theory|preparation|novelty|repertoire|transpose)\b', text, re.IGNORECASE)),
        'evaluation_terms': len(re.findall(r'\b(advantage|equal|better|worse|winning|losing|unclear|sharp)\b', text, re.IGNORECASE))
    }
    return features

def validate_entry_structure(entry: Dict) -> List[str]:
    """Validate the structure of a training entry."""
    errors = []
    
    # Check required fields
    if 'messages' not in entry:
        errors.append("Missing 'messages' field")
        return errors
    
    messages = entry['messages']
    if not isinstance(messages, list):
        errors.append("'messages' should be a list")
        return errors
    
    if len(messages) < 2:
        errors.append("Need at least user and assistant messages")
    
    # Check message structure
    for i, msg in enumerate(messages):
        if 'role' not in msg:
            errors.append(f"Message {i} missing 'role'")
        if 'content' not in msg:
            errors.append(f"Message {i} missing 'content'")
        
        role = msg.get('role', '')
        if role not in ['system', 'user', 'assistant']:
            errors.append(f"Invalid role '{role}' in message {i}")
    
    # Check conversation flow
    roles = [msg.get('role', '') for msg in messages]
    if roles[-1] != 'assistant':
        errors.append("Last message should be from assistant")
    
    return errors

def analyze_response_quality(assistant_content: str) -> Dict[str, float]:
    """Analyze the quality metrics of assistant responses."""
    word_count = len(assistant_content.split())
    sentence_count = len([s for s in assistant_content.split('.') if s.strip()])
    
    quality_metrics = {
        'word_count': word_count,
        'sentence_count': sentence_count,
        'avg_words_per_sentence': word_count / max(sentence_count, 1),
        'chess_content_density': sum(analyze_chess_accuracy(assistant_content).values()) / max(word_count, 1),
        'human_like_density': sum(analyze_human_like_features(assistant_content).values()) / max(word_count, 1),
        'response_complexity': len(set(assistant_content.lower().split())) / max(word_count, 1),  # unique words ratio
        'emotional_quotient': analyze_human_like_features(assistant_content)['emotional_expressions'] + analyze_human_like_features(assistant_content)['uncertainty_markers']
    }
    
    return quality_metrics

def identify_enhanced_entries(entry: Dict) -> bool:
    """Identify if an entry has been enhanced with human-like features."""
    assistant_content = ""
    for msg in entry.get('messages', []):
        if msg.get('role') == 'assistant':
            assistant_content = msg.get('content', '')
            break
    
    features = analyze_human_like_features(assistant_content)
    
    # Enhanced entries typically have multiple human-like features
    human_indicators = (
        features['uncertainty_markers'] > 0 or
        features['emotional_expressions'] > 0 or
        features['conversational_fillers'] > 1 or
        features['self_corrections'] > 0 or
        features['ellipses_pauses'] > 0 or
        features['sensory_details'] > 0 or
        features['memory_references'] > 0
    )
    
    # Also check for enhanced metadata
    enhanced_metadata = entry.get('meta', {}).get('source') == 'enhanced'
    
    return human_indicators or enhanced_metadata

def generate_quality_report(data: List[Dict]) -> str:
    """Generate a comprehensive quality report."""
    if not data:
        return "❌ No data to analyze"
    
    # Basic statistics
    total_entries = len(data)
    valid_entries = 0
    enhanced_entries = 0
    error_entries = 0
    
    # Quality metrics
    word_counts = []
    chess_densities = []
    human_densities = []
    emotional_quotients = []
    
    # Topic distribution
    topics = Counter()
    difficulties = Counter()
    sources = Counter()
    
    # Detailed analysis
    for entry in data:
        # Validate structure
        errors = validate_entry_structure(entry)
        if errors:
            error_entries += 1
            continue
        
        valid_entries += 1
        
        # Check if enhanced
        if identify_enhanced_entries(entry):
            enhanced_entries += 1
        
        # Extract assistant response
        assistant_content = ""
        for msg in entry.get('messages', []):
            if msg.get('role') == 'assistant':
                assistant_content = msg.get('content', '')
                break
        
        if assistant_content:
            quality = analyze_response_quality(assistant_content)
            word_counts.append(quality['word_count'])
            chess_densities.append(quality['chess_content_density'])
            human_densities.append(quality['human_like_density'])
            emotional_quotients.append(quality['emotional_quotient'])
        
        # Metadata analysis
        meta = entry.get('meta', {})
        topics[meta.get('topic', 'unknown')] += 1
        difficulties[meta.get('difficulty', 'unknown')] += 1
        sources[meta.get('source', 'unknown')] += 1
    
    # Generate report
    report = f"""
🏆 CAPABLANCA CHAMPIONSHIP TRAINING DATA QUALITY REPORT
======================================================

📊 DATASET OVERVIEW
Total Entries: {total_entries}
Valid Entries: {valid_entries} ({valid_entries/total_entries*100:.1f}%)
Enhanced Entries: {enhanced_entries} ({enhanced_entries/total_entries*100:.1f}%)
Error Entries: {error_entries} ({error_entries/total_entries*100:.1f}%)

📈 QUALITY METRICS
Average Word Count: {statistics.mean(word_counts):.1f} words
Word Count Range: {min(word_counts)} - {max(word_counts)} words
Chess Content Density: {statistics.mean(chess_densities):.3f}
Human-like Density: {statistics.mean(human_densities):.3f}
Average Emotional Quotient: {statistics.mean(emotional_quotients):.1f}

📚 TOPIC DISTRIBUTION
"""
    
    for topic, count in topics.most_common():
        percentage = count / total_entries * 100
        report += f"  {topic}: {count} entries ({percentage:.1f}%)\n"
    
    report += f"\n🎯 DIFFICULTY DISTRIBUTION\n"
    for difficulty, count in difficulties.most_common():
        percentage = count / total_entries * 100
        report += f"  {difficulty}: {count} entries ({percentage:.1f}%)\n"
    
    report += f"\n🔧 SOURCE DISTRIBUTION\n"
    for source, count in sources.most_common():
        percentage = count / total_entries * 100
        report += f"  {source}: {count} entries ({percentage:.1f}%)\n"
    
    # Quality assessment
    report += f"\n🏅 CHAMPIONSHIP READINESS ASSESSMENT\n"
    
    enhancement_score = enhanced_entries / total_entries
    quality_score = statistics.mean(human_densities) + statistics.mean(chess_densities)
    emotional_score = statistics.mean(emotional_quotients) / max(word_counts)
    
    overall_score = (enhancement_score * 0.4 + quality_score * 0.4 + emotional_score * 0.2) * 100
    
    report += f"Enhancement Coverage: {enhancement_score*100:.1f}% ({'✅ Excellent' if enhancement_score > 0.2 else '⚠️ Needs Improvement'})\n"
    report += f"Content Quality: {quality_score:.3f} ({'✅ High Quality' if quality_score > 0.1 else '⚠️ Needs Improvement'})\n"
    report += f"Emotional Intelligence: {emotional_score:.3f} ({'✅ Very Human' if emotional_score > 0.02 else '⚠️ Too Robotic'})\n"
    report += f"\n🎖️ OVERALL CHAMPIONSHIP SCORE: {overall_score:.1f}/100\n"
    
    if overall_score >= 85:
        report += "🏆 READY TO DEFEAT FISCHER! This dataset should create a superior AI.\n"
    elif overall_score >= 70:
        report += "🥈 Strong contender, may need minor improvements to guarantee victory.\n"
    else:
        report += "🥉 Needs significant enhancement to compete with top-tier AIs.\n"
    
    return report

def main():
    """Main validation and analysis function."""
    print("🔍 CAPABLANCA CHAMPIONSHIP DATA VALIDATOR")
    print("=" * 50)
    
    # Load the championship training data
    championship_data = load_jsonl('capablanca_training_data_ultimate_championship.jsonl')
    
    if not championship_data:
        print("❌ Failed to load championship data. Exiting.")
        return
    
    # Generate quality report
    report = generate_quality_report(championship_data)
    print(report)
    
    # Save report to file
    with open('CAPABLANCA_CHAMPIONSHIP_VALIDATION_REPORT.txt', 'w', encoding='utf-8') as f:
        f.write(report)
    
    print("\n📁 Detailed report saved to: CAPABLANCA_CHAMPIONSHIP_VALIDATION_REPORT.txt")
    
    # Quick validation check
    errors_found = 0
    for i, entry in enumerate(championship_data):
        errors = validate_entry_structure(entry)
        if errors:
            print(f"❌ Entry {i+1} errors: {', '.join(errors)}")
            errors_found += 1
    
    if errors_found == 0:
        print("✅ All entries passed structural validation!")
        print("\n🚀 READY FOR OPENAI FINE-TUNING!")
        print("\nNext steps:")
        print("1. Upload capablanca_training_data_championship.jsonl to OpenAI")
        print("2. Create fine-tuning job with GPT-4o-mini or GPT-4o")
        print("3. Monitor training progress")
        print("4. Deploy and test against Fischer AI")
        print("5. Celebrate victory! 🎉")
    else:
        print(f"⚠️ Found {errors_found} entries with structural issues. Please fix before fine-tuning.")

if __name__ == "__main__":
    main()