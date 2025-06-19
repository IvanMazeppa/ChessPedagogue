#!/usr/bin/env python3
"""
🎭 Claude 4 Personality Training Data Agent
===============================================

Automated generation of human-voice personality training data for chess masters.
Integrates with the proven ChessPedagogue methodology for "warts and all" personality creation.

Based on 3 months of research: Personality vs Facts separation is CRITICAL.
This agent generates ONLY voice, style, emotions - NO chess positions or facts.
"""

import os
import json
import time
import logging
from typing import Dict, List, Any, Optional
from pathlib import Path
from dataclasses import dataclass
from anthropic import Anthropic

# Configure logging with emojis for visibility
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

@dataclass
class PersonalityConfig:
    """Configuration for personality training data generation"""
    master_name: str
    complexity_level: int  # 1-10
    emotional_range: List[str]
    flaw_intensity: float  # 0.0-1.0
    training_examples: int
    include_warts_and_all: bool = True
    voice_style: str = ""
    emotional_mask: str = ""
    defensive_response: str = ""

class ClaudePersonalityAgent:
    """
    🎭 Claude 4 agent for generating human-like personality training data
    
    CRITICAL: This agent generates ONLY personality content - no chess facts!
    """
    
    def __init__(self, api_key: str = None):
        """Initialize Claude client with API key"""
        
        # Try multiple sources for API key
        self.api_key = (
            api_key or 
            os.getenv('ANTHROPIC_API_KEY') or 
            os.getenv('CLAUDE_API_KEY')
        )
        
        if not self.api_key:
            raise ValueError(
                "🔑 Claude API key required! Set ANTHROPIC_API_KEY environment variable "
                "or pass api_key parameter. Get your key at: https://console.anthropic.com/"
            )
        
        self.client = Anthropic(api_key=self.api_key)
        logger.info("🤖 Claude 4 Personality Agent initialized successfully!")
    
    def generate_personality_training_data(self, config: PersonalityConfig) -> Dict[str, Any]:
        """
        Generate comprehensive personality training data
        
        Returns training examples in format ready for fine-tuning
        """
        logger.info(f"🎭 Starting personality training data generation for {config.master_name}")
        logger.info(f"📊 Target: {config.training_examples} examples, complexity level {config.complexity_level}")
        
        # Generate different types of personality content
        training_data = {
            "metadata": {
                "master_name": config.master_name,
                "generated_date": time.strftime("%Y-%m-%d %H:%M:%S"),
                "agent_version": "1.0_claude4_warts_and_all",
                "complexity_level": config.complexity_level,
                "flaw_intensity": config.flaw_intensity,
                "emotional_range": config.emotional_range,
                "total_examples": 0
            },
            "training_examples": []
        }
        
        # Calculate distribution of different content types
        distribution = self._calculate_content_distribution(config.training_examples)
        
        # Generate each content type
        content_types = [
            ("triumphs_and_pride", "Greatest achievements and moments of pride"),
            ("failures_and_regrets", "Failures, mistakes, and deep regrets"),
            ("rivalries_and_relationships", "Feelings about other masters and competitors"),
            ("self_reflection", "Honest self-assessment and internal thoughts"),
            ("humor_and_personality", "Jokes, wit, and personality quirks"),
            ("philosophy_and_wisdom", "Life lessons and chess philosophy"),
            ("controversial_opinions", "Strong opinions and controversial takes"),
            ("vulnerability_moments", "Moments of doubt, fear, and uncertainty"),
            ("growth_and_change", "How views and personality evolved over time")
        ]
        
        for content_type, description in content_types:
            count = distribution.get(content_type, 0)
            if count > 0:
                logger.info(f"📝 Generating {count} examples: {description}")
                examples = self._generate_content_type(config, content_type, description, count)
                training_data["training_examples"].extend(examples)
        
        training_data["metadata"]["total_examples"] = len(training_data["training_examples"])
        
        logger.info(f"🎉 Successfully generated {len(training_data['training_examples'])} personality examples!")
        return training_data
    
    def _calculate_content_distribution(self, total_examples: int) -> Dict[str, int]:
        """Calculate how many examples of each content type to generate"""
        
        # Proven distribution based on 3 months of research
        percentages = {
            "triumphs_and_pride": 0.15,      # 15% - Balanced with failures
            "failures_and_regrets": 0.20,    # 20% - Critical for "warts and all"
            "rivalries_and_relationships": 0.15, # 15% - Important for dynamics
            "self_reflection": 0.15,          # 15% - Core personality insights
            "humor_and_personality": 0.10,    # 10% - Humanizing quirks
            "philosophy_and_wisdom": 0.10,    # 10% - Depth and wisdom
            "controversial_opinions": 0.05,   # 5% - Authentic strong views
            "vulnerability_moments": 0.05,    # 5% - Essential humanity
            "growth_and_change": 0.05         # 5% - Personal evolution
        }
        
        distribution = {}
        for content_type, percentage in percentages.items():
            distribution[content_type] = max(1, int(total_examples * percentage))
        
        return distribution
    
    def _generate_content_type(self, config: PersonalityConfig, content_type: str, 
                              description: str, count: int) -> List[Dict[str, str]]:
        """Generate specific type of personality content"""
        
        prompt = self._create_personality_prompt(config, content_type, description, count)
        
        try:
            response = self.client.messages.create(
                model="claude-3-5-sonnet-20241022",  # Claude 4 equivalent
                max_tokens=4000,
                temperature=0.8,  # Higher for creative personality content
                messages=[{
                    "role": "user",
                    "content": prompt
                }]
            )
            
            content = response.content[0].text.strip()
            
            # Parse the response into training examples
            examples = self._parse_claude_response(content, content_type)
            
            logger.info(f"✅ Generated {len(examples)} {content_type} examples")
            return examples
            
        except Exception as e:
            logger.error(f"❌ Error generating {content_type}: {e}")
            return []
    
    def _create_personality_prompt(self, config: PersonalityConfig, content_type: str, 
                                  description: str, count: int) -> str:
        """Create Claude prompt for personality content generation"""
        
        emotional_range = ", ".join(config.emotional_range)
        
        base_prompt = f"""
You are creating personality training data for {config.master_name}, focusing on {description}.

CRITICAL REQUIREMENTS:
- Generate ONLY voice, style, emotions, and personality content
- NO chess positions, FEN strings, or move analysis
- NO biographical facts, dates, or tournament results  
- NO technical chess content or opening theory
- Focus on HOW they think and feel, not WHAT they know

PERSONALITY PROFILE:
- Master: {config.master_name}
- Voice Style: {config.voice_style or "Authentic historical voice"}
- Emotional Range: {emotional_range}
- Emotional Mask: {config.emotional_mask or "Complex emotional layers"}
- Defensive Response: {config.defensive_response or "Natural defensive mechanisms"}
- Complexity Level: {config.complexity_level}/10
- Flaw Intensity: {config.flaw_intensity:.1%}
- Include "Warts and All": {config.include_warts_and_all}

CONTENT TYPE: {content_type}
DESCRIPTION: {description}

Generate {count} training examples in this format:

Example 1:
USER: [Appropriate question that would elicit this type of response]
ASSISTANT: [Personality response showing voice, emotions, and human complexity]

Example 2:
USER: [Different question]
ASSISTANT: [Another personality response with different emotional tone]

[Continue for all {count} examples...]

PERSONALITY GUIDELINES:
"""

        # Add content-type specific guidelines
        type_guidelines = {
            "triumphs_and_pride": """
- Show genuine pride but also humility
- Include moments of doubt even in success
- Reveal what success meant personally
- Show competitive fire and satisfaction
""",
            "failures_and_regrets": """
- Express genuine regret and disappointment  
- Show how failures shaped character
- Include self-criticism and lessons learned
- Reveal vulnerability behind public facade
""",
            "rivalries_and_relationships": """
- Express genuine feelings about other masters
- Include both respect and competitive tension
- Show personal reactions, not just professional
- Reveal jealousy, admiration, frustration
""",
            "self_reflection": """
- Show introspective and honest self-assessment
- Include contradictions and uncertainty
- Reveal internal struggles and growth
- Show both confidence and self-doubt
""",
            "humor_and_personality": """
- Show natural wit and personality quirks
- Include chess-related jokes and observations
- Reveal lighter side and human warmth
- Show unique mannerisms and speech patterns
""",
            "philosophy_and_wisdom": """
- Share life lessons learned from chess
- Express deeper meaning and purpose
- Show philosophical development over time
- Include both wisdom and uncertainty
""",
            "controversial_opinions": """
- Express strong personal opinions
- Show passionate conviction
- Include potentially unpopular views
- Reveal authentic personal beliefs
""",
            "vulnerability_moments": """
- Show genuine fear, doubt, and uncertainty
- Express moments of feeling overwhelmed
- Reveal insecurities and weaknesses
- Show authentic human vulnerability
""",
            "growth_and_change": """
- Show how personality evolved over time
- Express changing views and beliefs
- Reveal personal transformation
- Show both continuity and growth
"""
        }
        
        guidelines = type_guidelines.get(content_type, "- Show authentic human personality")
        
        warts_and_all_note = ""
        if config.include_warts_and_all:
            warts_and_all_note = """
CRITICAL "WARTS AND ALL" REQUIREMENTS:
- Include authentic human flaws and contradictions
- Show overconfidence, self-doubt, rivalry, jealousy
- Express regrets, mistakes, and poor decisions
- Reveal defensive mechanisms and coping strategies
- Make them human, not perfect chess gods
"""
        
        return base_prompt + guidelines + warts_and_all_note
    
    def _parse_claude_response(self, content: str, content_type: str) -> List[Dict[str, str]]:
        """Parse Claude's response into training examples"""
        
        examples = []
        lines = content.split('\n')
        current_user = None
        current_assistant = None
        
        for line in lines:
            line = line.strip()
            
            if line.startswith('USER:'):
                if current_user and current_assistant:
                    # Save previous example
                    examples.append({
                        "messages": [
                            {"role": "user", "content": current_user},
                            {"role": "assistant", "content": current_assistant}
                        ],
                        "content_type": content_type,
                        "training_type": "personality_voice_only"
                    })
                
                current_user = line[5:].strip()
                current_assistant = None
                
            elif line.startswith('ASSISTANT:'):
                current_assistant = line[10:].strip()
                
            elif current_assistant and line and not line.startswith('Example'):
                # Continue assistant response
                current_assistant += " " + line
        
        # Don't forget the last example
        if current_user and current_assistant:
            examples.append({
                "messages": [
                    {"role": "user", "content": current_user},
                    {"role": "assistant", "content": current_assistant}
                ],
                "content_type": content_type,
                "training_type": "personality_voice_only"
            })
        
        return examples
    
    def save_training_data(self, training_data: Dict[str, Any], output_dir: str = "personality_training") -> str:
        """Save training data to file in format ready for fine-tuning"""
        
        # Create output directory
        Path(output_dir).mkdir(exist_ok=True)
        
        master_name = training_data["metadata"]["master_name"]
        safe_name = master_name.lower().replace(' ', '_').replace("'", "")
        
        # Save comprehensive training data
        filename = f"{safe_name}_personality_training_data.json"
        filepath = Path(output_dir) / filename
        
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(training_data, f, ensure_ascii=False, indent=2)
        
        # Also save in OpenAI fine-tuning format (JSONL)
        fine_tuning_filename = f"{safe_name}_fine_tuning.jsonl"
        fine_tuning_filepath = Path(output_dir) / fine_tuning_filename
        
        with open(fine_tuning_filepath, 'w', encoding='utf-8') as f:
            for example in training_data["training_examples"]:
                json.dump(example["messages"], f)
                f.write('\n')
        
        logger.info(f"💾 Training data saved:")
        logger.info(f"   📊 Comprehensive data: {filepath}")
        logger.info(f"   🔧 Fine-tuning format: {fine_tuning_filepath}")
        logger.info(f"   📈 Total examples: {len(training_data['training_examples'])}")
        
        return str(filepath)
    
    def validate_personality_quality(self, training_data: Dict[str, Any]) -> Dict[str, Any]:
        """Use Claude to validate the quality of generated personality data"""
        
        logger.info("🧪 Running personality quality validation...")
        
        # Sample a few examples for validation
        examples = training_data["training_examples"][:5]
        sample_text = "\n\n".join([
            f"USER: {ex['messages'][0]['content']}\nASSISTANT: {ex['messages'][1]['content']}"
            for ex in examples
        ])
        
        validation_prompt = f"""
Evaluate this personality training data for {training_data['metadata']['master_name']}:

{sample_text}

Quality Assessment Criteria:
1. Does this sound human, not AI-generated?
2. Would a chess historian recognize this voice?
3. Are there appropriate contradictions and flaws?
4. Is this conversational, not encyclopedic?
5. Does this capture authentic personality?

Provide a detailed quality assessment with scores (1-10) for each criteria.
"""
        
        try:
            response = self.client.messages.create(
                model="claude-3-5-sonnet-20241022",
                max_tokens=1000,
                temperature=0.3,
                messages=[{
                    "role": "user", 
                    "content": validation_prompt
                }]
            )
            
            validation_result = {
                "validation_date": time.strftime("%Y-%m-%d %H:%M:%S"),
                "validator": "Claude 4",
                "assessment": response.content[0].text.strip(),
                "sample_size": len(examples),
                "total_examples": len(training_data["training_examples"])
            }
            
            logger.info("✅ Personality quality validation completed")
            return validation_result
            
        except Exception as e:
            logger.error(f"❌ Validation error: {e}")
            return {"error": str(e)}

def main():
    """
    Main function for interactive personality training data generation
    """
    print("🎭 Claude 4 Personality Training Data Agent")
    print("=" * 50)
    print("🧠 Automated 'Warts and All' Personality Generation")
    print("=" * 50)
    
    # Check for API key
    api_key = os.getenv('ANTHROPIC_API_KEY') or os.getenv('CLAUDE_API_KEY')
    if not api_key:
        print("\n🔑 CLAUDE API KEY REQUIRED")
        print("Get your API key at: https://console.anthropic.com/")
        api_key = input("Enter your Claude API key: ").strip()
        if not api_key:
            print("❌ API key required. Exiting.")
            return
    
    try:
        agent = ClaudePersonalityAgent(api_key)
        
        # Get master configuration
        print("\n📋 Master Configuration")
        master_name = input("Enter chess master name: ").strip()
        if not master_name:
            print("❌ Master name required")
            return
        
        # Configuration with smart defaults
        config = PersonalityConfig(
            master_name=master_name,
            complexity_level=int(input("Complexity level (1-10, default 7): ") or "7"),
            emotional_range=["Joy/Triumph", "Frustration/Anger", "Self-Doubt", "Regret/Reflection", "Humor/Wit"],
            flaw_intensity=float(input("Flaw intensity (0.0-1.0, default 0.3): ") or "0.3"),
            training_examples=int(input("Number of training examples (default 350): ") or "350"),
            include_warts_and_all=True,
            voice_style=input("Voice style (optional): ").strip(),
            emotional_mask=input("Emotional mask (optional): ").strip(),
            defensive_response=input("Defensive response pattern (optional): ").strip()
        )
        
        print(f"\n🚀 Generating personality training data for {master_name}...")
        print(f"📊 Target: {config.training_examples} examples, {config.complexity_level}/10 complexity")
        print(f"🩹 Warts and all: {config.include_warts_and_all}")
        print(f"⚡ Flaw intensity: {config.flaw_intensity:.1%}")
        
        # Generate training data
        training_data = agent.generate_personality_training_data(config)
        
        # Save results
        output_file = agent.save_training_data(training_data)
        
        # Optional quality validation
        validate = input("\n🧪 Run quality validation? (y/n, default y): ").strip().lower()
        if validate != 'n':
            validation = agent.validate_personality_quality(training_data)
            
            # Save validation results
            validation_file = output_file.replace('.json', '_validation.json')
            with open(validation_file, 'w') as f:
                json.dump(validation, f, indent=2)
            
            print(f"✅ Validation saved: {validation_file}")
        
        print(f"\n🎉 SUCCESS! Personality training data generated!")
        print(f"📁 Output: {output_file}")
        print(f"🎭 Ready for fine-tuning with voice and personality only!")
        print(f"⚠️ Remember: This contains NO chess facts - keep personality separate!")
        
    except Exception as e:
        logger.error(f"❌ Fatal error: {e}")
        print(f"\n❌ Error: {e}")

if __name__ == "__main__":
    main()