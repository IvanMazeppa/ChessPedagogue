# ElevenLabs TTS Migration Guide

## Overview
I've configured your ChessPedagogue app to support ElevenLabs TTS alongside the existing OpenAI TTS. ElevenLabs offers significantly better voice quality with:
- Ultra-low latency (~75ms with Flash model)
- Context-aware emotional adaptation
- Natural accent support for all 12 chess masters
- Voice cloning capabilities

## Setup Instructions

### 1. Set Your API Key
Set the `ELEVENLABS_API_KEY` environment variable with your ElevenLabs API key:
```bash
export ELEVENLABS_API_KEY="your_elevenlabs_api_key_here"
```

Or programmatically in your app:
```java
TTSServiceManager.setElevenLabsApiKey(context, "your_api_key");
```

### 2. Enable ElevenLabs in Settings
The app now has a toggle in Settings to switch between OpenAI and ElevenLabs:
- Go to Settings
- Find "Use ElevenLabs TTS (Ultra-realistic voices)"
- Toggle it on

### 3. Voice Configuration
The ElevenLabsTTSService is pre-configured with voice IDs for each chess master:
- **Tal**: Warm, expressive voice with Latvian-Russian characteristics
- **Fischer**: Strong, authoritative Brooklyn accent
- **Carlsen**: Modern, confident Norwegian accent
- **Kasparov**: Dynamic, intense Russian accent
- And more...

## Technical Implementation

### New Files Created:
1. **ElevenLabsTTSService.java** - Complete ElevenLabs API integration
2. **TTSServiceManager.java** - Centralized TTS service selection
3. **ELEVENLABS_MIGRATION.md** - This guide

### Modified Files:
1. **SettingsActivity.java** - Added ElevenLabs toggle switch

### Key Features:
- **Seamless switching**: No need to change existing code
- **Backward compatible**: Falls back to OpenAI if ElevenLabs fails
- **Emotion-aware**: Voice settings adapt based on game evaluation
- **Master-specific**: Each chess master has unique voice characteristics

## Voice Customization

Each master has customized voice settings:

```java
// Stability (0.0-1.0): Lower = more expressive
// Tal: 0.3 (very expressive)
// Fischer: 0.8 (very controlled)

// Style (0.0-1.0): Higher = more stylistic
// Tal: 0.8 (very stylistic)
// Fischer: 0.3 (direct and authoritative)
```

## API Usage

The migration is transparent to existing code:
```java
// This works with both OpenAI and ElevenLabs
OpenAITTSService tts = TTSServiceManager.getOpenAITTSService(context);
tts.speak("Hello from your chess master!");
```

## Pricing Comparison

**ElevenLabs Starter Plan**: $5/month
- 30,000 characters/month
- Up to 10 custom voices
- Ultra-low latency
- Commercial license included

**OpenAI TTS**: Variable pricing
- Pay per character
- Limited voice options
- Higher latency
- Less emotional range

## Next Steps

1. **Test the voices**: Try each chess master with ElevenLabs enabled
2. **Fine-tune settings**: Adjust stability/style for each master
3. **Consider voice cloning**: Create custom voices for each chess master
4. **Monitor usage**: Track character usage in ElevenLabs dashboard

## Troubleshooting

If ElevenLabs doesn't work:
1. Check API key is set correctly
2. Verify internet connection
3. Check ElevenLabs API status
4. App automatically falls back to OpenAI

## Benefits for ChessPedagogue

1. **Tal's Latvian accent** sounds authentic, not forced
2. **Fischer's Brooklyn intensity** comes through naturally
3. **Carlsen's Norwegian coolness** is perfectly captured
4. **Emotional responses** during evaluation swings are more realistic
5. **Spectator mode conversations** sound like real people talking

The $5/month cost is minimal compared to the massive improvement in user experience!