# Conversational Banter Improvements

## Overview
Enhanced the SpectatorConversationOrchestrator to create more engaging, emotionally-aware conversations between chess masters with improved voice modulation and diverse dialogue patterns.

## Key Improvements

### 1. ElevenLabs TTS Integration
- **Switched from OpenAI TTS to ElevenLabs** for spectator mode
- **Model Selection**: Uses "eleven_turbo_v2_5" for enhanced quality in spectator mode
- **Context-Aware**: Automatically sets usage context for optimal model selection
- **Fallback Support**: Gracefully falls back to OpenAI TTS if ElevenLabs unavailable

### 2. Emotional Voice Modulation
- **Evaluation-Based Emotions**: Detects 8 different emotional states based on position evaluation
  - `thrilled` - Major advantage (>3.0 eval change or >4.0 position)
  - `pleased` - Good improvement (>1.5 eval change or >2.0 position)
  - `satisfied` - Minor improvement (>0.5 eval change)
  - `concerned` - Worrying development (>1.0 negative change)
  - `frustrated` - Significant setback (>2.0 negative change)
  - `desperate` - Major disadvantage (>3.0 negative change or <-4.0 position)
  - `impressed`, `critical`, `intrigued` - Based on conversation content

- **Voice Adjustment**: 
  - ElevenLabs: Adjusts stability settings per master (Tal: 0.35, Fischer: 0.70)
  - OpenAI: Uses reflection to access emotional TTS methods when available

### 3. Enhanced Conversation Prompts

#### Initial Prompts - Personality-Specific
- **Tal**: "Facing {opponent}! I can already sense the tactical storms brewing..."
- **Fischer**: "Playing {opponent}. I've prepared everything. Time to prove who plays the most accurate chess."
- **Carlsen**: "Playing {opponent} today. Let's see where the game takes us - I'm ready for anything."

#### Response Prompts - Emotion-Aware
- **When Winning**: "You're doing well! {opponent} says: '{statement}'. Share your confident response!"
- **When Losing**: "You're under pressure! {opponent} taunts: '{statement}'. Defend yourself!"
- **Neutral**: Multiple varied formats to prevent repetition

#### Personality Encouragements
- **Fischer**: ["Only perfect moves matter!", "Show them the objective truth!", "Precision beats everything!"]
- **Tal**: ["Find the creative angle!", "Where's the magic?", "Show them chess poetry!"]
- **Carlsen**: ["Keep it practical!", "Modern chess wisdom prevails!", "Pragmatic excellence wins!"]

### 4. Conversation Flow Improvements
- **More Permissive Continuation**: 
  - Always continues for first 3 turns
  - 50% random chance to continue after turn 4
  - Triggers on more keywords: "you", "your", questions, exclamations

- **Timeout Recovery**: Automatically resets stuck conversations after 30 seconds

- **Clean Response Processing**: Removes system instruction leaks and unwanted patterns

### 5. Technical Enhancements
- **Speech Queue Management**: Prevents voice overlap with proper queueing
- **Master Voice Switching**: Temporarily switches voice for each speaker
- **Error Resilience**: Fallbacks at multiple levels for robust operation

## Testing Recommendations

1. **Emotional Responses**: Make moves that cause large evaluation swings (>2.0) to trigger emotional voices
2. **Personality Variety**: Test different master pairings (Tal vs Fischer shows great contrast)
3. **Long Conversations**: Let conversations run 4-6 turns to see natural flow
4. **Voice Quality**: Compare ElevenLabs vs OpenAI quality in spectator mode

## Configuration
- ElevenLabs API key should be set in SharedPreferences
- TTS preference can be set globally via TTSServiceManager
- Spectator mode automatically uses optimal settings

## Result
The conversational system now creates more natural, emotionally-aware banter between chess masters with:
- Reduced repetitive phrases
- Personality-specific dialogue patterns  
- Emotional voice modulation based on game state
- Better conversation flow and continuation
- Higher quality voice synthesis with ElevenLabs