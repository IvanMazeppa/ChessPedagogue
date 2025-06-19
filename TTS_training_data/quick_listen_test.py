import os
import pygame
import librosa
import numpy as np
from pathlib import Path
import time

def quick_audio_test(file_path):
    """Quick test to play and analyze an audio file."""
    print(f"\n🎧 Testing: {Path(file_path).name}")
    
    try:
        # Load and analyze
        y, sr = librosa.load(file_path, sr=None)
        duration = len(y) / sr
        
        # Basic voice analysis
        f0, voiced_flag, voiced_probs = librosa.pyin(y, fmin=50, fmax=400, sr=sr)
        f0_clean = f0[voiced_flag]
        
        if len(f0_clean) > 0:
            mean_pitch = np.nanmean(f0_clean)
            print(f"   Duration: {duration:.1f}s")
            print(f"   Average pitch: {mean_pitch:.1f} Hz")
            
            # Voice characteristics guess
            if 80 <= mean_pitch <= 150:
                voice_guess = "🎯 Likely Kasparov (deeper voice)"
            elif 150 <= mean_pitch <= 220:
                voice_guess = "🤔 Could be Lex (higher pitch)"
            else:
                voice_guess = "❓ Unclear"
            
            print(f"   Voice guess: {voice_guess}")
        
        # Play the audio
        pygame.mixer.init()
        pygame.mixer.music.load(file_path)
        print(f"   ▶️  Playing audio... (press Enter when done listening)")
        pygame.mixer.music.play()
        
        input()  # Wait for user input
        pygame.mixer.music.stop()
        pygame.mixer.quit()
        
        # Get user feedback
        rating = input("   Rate quality (1-5, 5=perfect for voice cloning): ")
        is_kasparov = input("   Is this clearly Kasparov? (y/n): ").lower().startswith('y')
        has_interruptions = input("   Any Lex interruptions? (y/n): ").lower().startswith('y')
        
        return {
            'file': Path(file_path).name,
            'duration': duration,
            'rating': rating,
            'is_kasparov': is_kasparov,
            'has_interruptions': has_interruptions
        }
        
    except Exception as e:
        print(f"   ❌ Error: {e}")
        return None

def test_training_ready_clips():
    """Test all training ready clips."""
    base_dir = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/TTS_training_data/garry_kasparov_voice_extraction"
    
    print("🎯 TESTING TRAINING-READY CLIPS")
    print("="*50)
    
    training_dir = Path(base_dir) / "training_ready"
    clips = list(training_dir.glob("*.wav"))
    
    results = []
    
    for i, clip in enumerate(clips[:5]):  # Test first 5
        print(f"\n📁 Clip {i+1}/{len(clips)}")
        result = quick_audio_test(str(clip))
        if result:
            results.append(result)
        
        continue_test = input("Continue to next clip? (y/n): ").lower().startswith('y')
        if not continue_test:
            break
    
    # Summary
    print("\n📊 SUMMARY:")
    print("="*30)
    good_clips = [r for r in results if r['is_kasparov'] and not r['has_interruptions']]
    print(f"Pure Kasparov clips: {len(good_clips)}/{len(results)}")
    
    if good_clips:
        print("\n🎯 Best clips for voice cloning:")
        for clip in good_clips:
            print(f"   ✅ {clip['file']} - {clip['duration']:.1f}s - Rating: {clip['rating']}")

if __name__ == "__main__":
    try:
        test_training_ready_clips()
    except KeyboardInterrupt:
        print("\n👋 Testing stopped by user")
    except Exception as e:
        print(f"\n❌ Error: {e}")
        print("💡 Make sure pygame and librosa are installed:")
        print("   pip install pygame librosa soundfile")