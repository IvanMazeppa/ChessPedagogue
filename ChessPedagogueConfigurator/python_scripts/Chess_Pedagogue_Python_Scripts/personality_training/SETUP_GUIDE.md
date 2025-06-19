# 🎭 Claude Personality Agent Setup Guide

## Step 1: Get Your Claude API Key

1. **Go to**: https://console.anthropic.com/
2. **Sign up/Login** with your account
3. **Go to API Keys** section
4. **Create New Key** 
5. **Copy the key** (starts with `sk-ant-`)

⚠️ **Important**: This is different from your Claude.ai subscription - you need API access for automation.

## Step 2: Install Required Dependencies

Open your terminal/command prompt and run:

```bash
pip install anthropic
```

If you don't have pip or Python, install Python first from: https://python.org

## Step 3: Set Your API Key (Choose One Method)

### Option A: Environment Variable (Recommended)
```bash
# Windows (Command Prompt)
set ANTHROPIC_API_KEY=sk-ant-your-key-here

# Windows (PowerShell)
$env:ANTHROPIC_API_KEY="sk-ant-your-key-here"

# Mac/Linux
export ANTHROPIC_API_KEY=sk-ant-your-key-here
```

### Option B: Create .env File
Create a file called `.env` in the same folder as the script:
```
ANTHROPIC_API_KEY=sk-ant-your-key-here
```

### Option C: Enter When Prompted
The script will ask for your API key if it can't find it automatically.

## Step 4: Run the Agent

Navigate to the script folder and run:

```bash
cd /path/to/ChessPedagogueConfigurator/python_scripts/Chess_Pedagogue_Python_Scripts/personality_training/
python claude_personality_agent.py
```

## Step 5: Configure Your Master

The script will ask you:

1. **Master name**: e.g., "Aaron Nimzowitsch"
2. **Complexity level**: 1-10 (recommend 7-8)
3. **Flaw intensity**: 0.0-1.0 (recommend 0.3)
4. **Training examples**: Number to generate (recommend 350)
5. **Voice style**: Optional description
6. **Emotional patterns**: Optional customization

## Step 6: Generated Files

The agent creates:
- `{master_name}_personality_training_data.json` - Full data with metadata
- `{master_name}_fine_tuning.jsonl` - Ready for OpenAI fine-tuning
- `{master_name}_validation.json` - Quality assessment

## Example Run

```
🎭 Claude 4 Personality Training Data Agent
==================================================
🧠 Automated 'Warts and All' Personality Generation
==================================================

📋 Master Configuration
Enter chess master name: Aaron Nimzowitsch
Complexity level (1-10, default 7): 8
Flaw intensity (0.0-1.0, default 0.3): 0.4
Number of training examples (default 350): 400
Voice style (optional): Intellectual, slightly pedantic
Emotional mask (optional): Hides sensitivity behind superiority
Defensive response pattern (optional): Becomes theoretical when challenged

🚀 Generating personality training data for Aaron Nimzowitsch...
📊 Target: 400 examples, 8/10 complexity
🩹 Warts and all: True
⚡ Flaw intensity: 40.0%

📝 Generating 60 examples: Greatest achievements and moments of pride
✅ Generated 12 triumphs_and_pride examples
📝 Generating 80 examples: Failures, mistakes, and deep regrets
✅ Generated 15 failures_and_regrets examples
[... continues for all content types ...]

🎉 SUCCESS! Personality training data generated!
📁 Output: nimzowitsch_personality_training_data.json
🎭 Ready for fine-tuning with voice and personality only!
⚠️ Remember: This contains NO chess facts - keep personality separate!
```

## Integration with Your Existing Scripts

After generating personality data, use it with your existing pipeline:

1. **Personality Fine-Tuning**: Use the generated `.jsonl` file
2. **Vector Store**: Use your existing `chunked_fen_db_creator_v5.py` for facts
3. **Game Data**: Use your existing `full_game_fen_generator_v5.py`
4. **Orchestration**: Combine both in your Master Builder GUI

## Troubleshooting

### "Module not found: anthropic"
```bash
pip install anthropic
```

### "API key not found"
Make sure you set the environment variable or create the .env file.

### "Permission denied"
Make sure the script file is executable:
```bash
chmod +x claude_personality_agent.py
```

### "Rate limit exceeded"
Claude has generous rate limits, but if you hit them, the script will wait and retry.

## Cost Estimation

- **Claude 4 API**: ~$3-15 per master (depending on examples)
- **Much cheaper** than manual GPT-4.1 requests
- **Much faster** - generates 350 examples in ~5-10 minutes

## Next Steps

1. **Generate personality data** for your first master
2. **Fine-tune** using OpenAI's fine-tuning API
3. **Create vector store** using your existing scripts  
4. **Integrate** both into your Master Builder GUI
5. **Test** the complete human-like master!

---

🎯 **Remember**: This agent generates ONLY personality content - no chess positions or facts. That's the secret to human-like AI masters!