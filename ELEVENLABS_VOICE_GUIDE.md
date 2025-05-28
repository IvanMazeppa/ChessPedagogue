# ElevenLabs Voice Configuration Guide

## Current Voice Mappings

I've configured the chess masters to use male voices from ElevenLabs. However, you may want to customize these based on your preferences.

### How to Find Voice IDs

1. **Go to ElevenLabs Voice Library**: https://elevenlabs.io/app/voice-library
2. **Browse available voices** and listen to samples
3. **Get Voice ID**: When you find a voice you like, click on it and look for the voice ID
4. **Update the mapping** in `ElevenLabsTTSService.java`

### Current Configuration

The voices are configured with these settings based on ElevenLabs documentation:

- **Stability**: 0.35-0.65 (lower = more expressive, higher = more consistent)
- **Similarity Boost**: 0.75 (recommended default)
- **Style**: 0.0 (ElevenLabs recommends keeping at 0 for stability)

### Voice Characteristics by Master

**Tal (Adam)**: 
- Stability: 0.35 (more expressive)
- Passionate, enthusiastic delivery

**Fischer (Clyde)**:
- Stability: 0.65 (more consistent)
- Intense, authoritative American voice

**Carlsen (Daniel)**:
- Stability: 0.5 (balanced)
- Modern, confident delivery

**Kasparov (Josh)**:
- Stability: 0.4 (dynamic)
- Intense, passionate delivery

### Customization Tips

1. **For accents**: Choose voices from the Voice Library that naturally have the accent you want
2. **For personality**: Adjust the stability parameter (lower for more expressive, higher for consistent)
3. **Avoid style parameter**: ElevenLabs recommends keeping it at 0 for best results

### Important Notes

- ElevenLabs doesn't support accent instructions like OpenAI
- The quality comes from the voice selection itself
- Clone voices or use Voice Design for custom voices
- The Flash model provides ~75ms latency

### Voice Library Categories

Look for voices in these categories:
- **Narration**: Good for analytical masters (Kramnik, Botvinnik)
- **Characters**: Good for expressive masters (Tal, Kasparov)
- **News**: Good for authoritative masters (Fischer, Karpov)

### Testing Voices

To test a new voice:
1. Update the voice ID in `MASTER_VOICE_IDS` map
2. Rebuild the app
3. Play a spectator game with that master

The natural quality of ElevenLabs voices means you don't need complex instructions - just pick the right voice!