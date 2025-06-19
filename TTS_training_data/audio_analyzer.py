import os
import librosa
import numpy as np
from pathlib import Path
import soundfile as sf

def analyze_segment(file_path):
    """Analyze a single audio segment for voice characteristics."""
    try:
        # Load audio
        y, sr = librosa.load(file_path, sr=22050)
        duration = len(y) / sr
        
        if duration < 1.0:
            return None
        
        # Basic voice analysis
        # 1. Pitch analysis for speaker identification
        f0, voiced_flag, voiced_probs = librosa.pyin(y, fmin=50, fmax=400, sr=sr)
        f0_clean = f0[voiced_flag]
        
        if len(f0_clean) == 0:
            return None
        
        mean_pitch = np.nanmean(f0_clean)
        pitch_std = np.nanstd(f0_clean)
        
        # 2. Energy analysis for speech detection
        rms = librosa.feature.rms(y=y)[0]
        mean_energy = np.mean(rms)
        energy_std = np.std(rms)
        
        # 3. Spectral characteristics
        spectral_centroids = librosa.feature.spectral_centroid(y=y, sr=sr)[0]
        mean_spectral_centroid = np.mean(spectral_centroids)
        
        # 4. Speaking pattern analysis (gaps, continuity)
        # Detect speech segments
        voice_threshold = np.percentile(rms, 30)
        speech_frames = rms > voice_threshold
        
        # Count speech vs silence
        speech_ratio = np.sum(speech_frames) / len(speech_frames)
        
        # Detect speaker changes (rough estimate)
        # Look for significant pitch changes that might indicate speaker switches
        if len(f0_clean) > 10:
            pitch_segments = np.array_split(f0_clean, min(5, len(f0_clean)//10))
            segment_means = [np.nanmean(seg) for seg in pitch_segments if len(seg) > 0]
            pitch_variation = np.std(segment_means) if len(segment_means) > 1 else 0
        else:
            pitch_variation = 0
        
        # Speaker identification heuristics
        # Kasparov: Russian accent, likely deeper voice (lower pitch), measured speech
        # Lex: American accent, different pitch pattern, faster speech sometimes
        
        kasparov_score = 0
        speaker_notes = []
        
        # Pitch scoring (Kasparov typically has deeper voice)
        if 80 <= mean_pitch <= 150:
            kasparov_score += 3
            speaker_notes.append("good pitch for Kasparov")
        elif 150 <= mean_pitch <= 200:
            kasparov_score += 1
            speaker_notes.append("possible Kasparov or Lex")
        else:
            speaker_notes.append("pitch outside typical range")
        
        # Speech continuity (Kasparov often speaks in longer, thoughtful segments)
        if speech_ratio > 0.7:
            kasparov_score += 2
            speaker_notes.append("continuous speech")
        elif speech_ratio > 0.5:
            kasparov_score += 1
            speaker_notes.append("moderate speech density")
        
        # Pitch stability (less variation might indicate single speaker)
        if pitch_variation < 15:
            kasparov_score += 2
            speaker_notes.append("stable pitch - likely single speaker")
        elif pitch_variation < 25:
            kasparov_score += 1
            speaker_notes.append("moderate pitch variation")
        else:
            speaker_notes.append("high pitch variation - possible multiple speakers")
        
        # Duration preference (10-30 seconds ideal)
        if 10 <= duration <= 30:
            kasparov_score += 2
            speaker_notes.append("ideal duration for training")
        elif 8 <= duration <= 35:
            kasparov_score += 1
            speaker_notes.append("good duration")
        
        return {
            'filename': Path(file_path).name,
            'duration': duration,
            'mean_pitch': mean_pitch,
            'pitch_std': pitch_std,
            'pitch_variation': pitch_variation,
            'mean_energy': mean_energy,
            'speech_ratio': speech_ratio,
            'spectral_centroid': mean_spectral_centroid,
            'kasparov_score': kasparov_score,
            'speaker_notes': speaker_notes,
            'likely_speaker': 'Kasparov' if kasparov_score >= 5 else 'Mixed/Lex' if kasparov_score >= 3 else 'Unclear'
        }
        
    except Exception as e:
        print(f"Error analyzing {file_path}: {e}")
        return None

def scan_segments(segments_dir):
    """Scan all segments and identify best Kasparov clips."""
    segments_path = Path(segments_dir)
    audio_files = list(segments_path.glob("*.wav"))
    
    print(f"🔍 Scanning {len(audio_files)} segments for Kasparov voice identification...")
    print("="*80)
    
    results = []
    
    for i, audio_file in enumerate(audio_files):
        if i % 20 == 0:
            print(f"Analyzed {i}/{len(audio_files)} segments...")
        
        analysis = analyze_segment(str(audio_file))
        if analysis:
            results.append(analysis)
    
    # Sort by Kasparov score
    results.sort(key=lambda x: x['kasparov_score'], reverse=True)
    
    print(f"\n✅ Analysis complete! Found {len(results)} valid segments")
    print("\n🎯 TOP KASPAROV CANDIDATES (likely solo Kasparov speech):")
    print("="*80)
    
    kasparov_candidates = [r for r in results if r['likely_speaker'] == 'Kasparov']
    
    for i, result in enumerate(kasparov_candidates[:15]):  # Top 15
        print(f"\n#{i+1}: {result['filename']}")
        print(f"   Duration: {result['duration']:.1f}s")
        print(f"   Pitch: {result['mean_pitch']:.1f} Hz (±{result['pitch_std']:.1f})")
        print(f"   Speech ratio: {result['speech_ratio']:.2f}")
        print(f"   Kasparov score: {result['kasparov_score']}/10")
        print(f"   Notes: {', '.join(result['speaker_notes'])}")
    
    print(f"\n📊 SUMMARY:")
    print(f"   Likely Kasparov segments: {len(kasparov_candidates)}")
    print(f"   Mixed/Unclear segments: {len(results) - len(kasparov_candidates)}")
    print(f"   Best candidates for training: {len([r for r in kasparov_candidates if r['kasparov_score'] >= 7])}")
    
    # Also show a few mixed/unclear for comparison
    mixed_segments = [r for r in results if r['likely_speaker'] != 'Kasparov'][:5]
    if mixed_segments:
        print(f"\n👥 MIXED/LEX SEGMENTS (for comparison):")
        print("="*50)
        for result in mixed_segments:
            print(f"{result['filename']}: {result['duration']:.1f}s, pitch: {result['mean_pitch']:.1f}Hz, score: {result['kasparov_score']}")
    
    return kasparov_candidates

if __name__ == "__main__":
    segments_dir = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/TTS_training_data/garry_kasparov_voice_extraction/segments"
    kasparov_segments = scan_segments(segments_dir)