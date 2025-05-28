# ElevenLabs Setup Instructions

## Quick Start

1. **Replace the API key placeholder** in `MainActivity.java` line 267:
   ```java
   ElevenLabsConfig.setApiKey(this, "YOUR_ELEVENLABS_API_KEY_HERE");
   ```
   Replace `"YOUR_ELEVENLABS_API_KEY_HERE"` with your actual ElevenLabs API key.

2. **Build and run the app**

3. **The app will automatically use ElevenLabs** - no need to go to settings!

## Verification

You should see this in your logs when the app starts:
```
MainActivity: ✅ ElevenLabs TTS enabled
```

And when TTS is used, you'll see:
```
TTSServiceManager: 🎤 Using ElevenLabs TTS Service
```

## Manual Toggle (Optional)

If you want to switch between OpenAI and ElevenLabs:
1. Go to Settings
2. Look for "Use ElevenLabs TTS (Ultra-realistic voices)"
3. Toggle on/off as needed

## Troubleshooting

If you still see OpenAI TTS being used:
1. Make sure you replaced the API key placeholder
2. Check that the key is valid
3. Try uninstalling and reinstalling the app
4. Check logs for any error messages

## What You'll Get

With ElevenLabs enabled:
- **Tal**: Warm Latvian-Russian accent with expressive emotion
- **Fischer**: Authentic Brooklyn/New York intensity
- **Carlsen**: Natural Norwegian confidence
- **Kasparov**: Dynamic Russian passion
- All voices respond with ~75ms latency (ultra-fast!)

## Security Note

For production, don't hardcode the API key. Consider:
- Using a secure config file
- Encrypting the key
- Using a backend service to provide the key