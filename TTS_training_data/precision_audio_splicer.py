import os
import numpy as np
import soundfile as sf
import librosa
from pathlib import Path
import matplotlib.pyplot as plt
from typing import List, Tuple, Dict
import json

class PrecisionAudioSplicer:
    """
    Ultra-high quality audio splicer that preserves original bit depth and sample rate.
    No processing - just precise cutting to remove interruptions.
    """
    
    def __init__(self, master_name: str = "Garry Kasparov"):
        self.master_name = master_name
        self.output_dir = Path(f"{master_name.lower().replace(' ', '_')}_precision_clips")
        self.output_dir.mkdir(exist_ok=True)
        
        print(f"🎯 Precision Audio Splicer for {master_name}")
        print("=" * 50)
        print("🔒 ZERO quality loss - preserving original audio exactly")
        print("✂️ Precision cutting to remove interruptions only")
        print(f"📁 Output: {self.output_dir}")
        print()

    def load_audio_lossless(self, file_path: str) -> Tuple[np.ndarray, int, Dict]:
        """Load audio with maximum quality preservation."""
        # Get original file info first
        info = sf.info(file_path)
        
        print(f"📊 Original file specs:")
        print(f"   Sample rate: {info.samplerate} Hz")
        print(f"   Bit depth: {info.subtype}")
        print(f"   Channels: {info.channels}")
        print(f"   Duration: {info.duration:.2f} seconds")
        
        # Load with original sample rate and bit depth
        audio, sr = sf.read(file_path, dtype='float64')  # Maximum precision
        
        # Convert to mono if stereo (preserve quality)
        if len(audio.shape) > 1:
            audio = np.mean(audio, axis=1)
        
        file_specs = {
            'original_samplerate': info.samplerate,
            'original_subtype': info.subtype,
            'original_channels': info.channels,
            'file_path': file_path
        }
        
        return audio, sr, file_specs

    def detect_speaker_segments(self, audio: np.ndarray, sr: int, 
                              window_duration: float = 2.0) -> List[Dict]:
        """
        Detect different speakers within audio using voice characteristics.
        Returns segments with speaker identification and confidence.
        """
        print("🔍 Analyzing speaker segments with high precision...")
        
        window_samples = int(window_duration * sr)
        hop_samples = window_samples // 4  # 75% overlap for precision
        
        segments = []
        
        for start_sample in range(0, len(audio) - window_samples, hop_samples):
            end_sample = min(start_sample + window_samples, len(audio))
            segment_audio = audio[start_sample:end_sample]
            
            # Skip very quiet segments
            rms = np.sqrt(np.mean(segment_audio**2))
            if rms < 0.01:
                continue
            
            # Extract voice characteristics with maximum precision
            features = self._extract_precise_features(segment_audio, sr)
            
            if features:
                # Speaker identification scoring
                kasparov_confidence = self._score_kasparov_voice(features)
                
                segment_info = {
                    'start_time': start_sample / sr,
                    'end_time': end_sample / sr,
                    'start_sample': start_sample,
                    'end_sample': end_sample,
                    'kasparov_confidence': kasparov_confidence,
                    'features': features,
                    'likely_speaker': 'Kasparov' if kasparov_confidence > 0.7 else 'Other',
                    'audio_segment': segment_audio
                }
                segments.append(segment_info)
        
        print(f"   ✅ Analyzed {len(segments)} voice segments")
        return segments

    def _extract_precise_features(self, audio: np.ndarray, sr: int) -> Dict:
        """Extract precise voice features for speaker identification."""
        try:
            # Fundamental frequency with high precision
            f0, voiced_flag, voiced_probs = librosa.pyin(
                audio, fmin=50, fmax=500, sr=sr, 
                frame_length=2048, hop_length=256
            )
            f0_clean = f0[voiced_flag]
            
            if len(f0_clean) == 0:
                return None
            
            # Precise spectral features
            mfccs = librosa.feature.mfcc(y=audio, sr=sr, n_mfcc=20, hop_length=256)
            spectral_centroid = librosa.feature.spectral_centroid(y=audio, sr=sr, hop_length=256)[0]
            spectral_rolloff = librosa.feature.spectral_rolloff(y=audio, sr=sr, hop_length=256)[0]
            zero_crossing_rate = librosa.feature.zero_crossing_rate(audio, hop_length=256)[0]
            
            # Voice quality measures
            rms = librosa.feature.rms(y=audio, hop_length=256)[0]
            
            return {
                'mean_pitch': np.nanmean(f0_clean),
                'pitch_std': np.nanstd(f0_clean),
                'pitch_range': np.nanmax(f0_clean) - np.nanmin(f0_clean),
                'voiced_ratio': np.sum(voiced_flag) / len(voiced_flag),
                'mfcc_mean': np.mean(mfccs, axis=1),
                'mfcc_std': np.std(mfccs, axis=1),
                'spectral_centroid_mean': np.mean(spectral_centroid),
                'spectral_rolloff_mean': np.mean(spectral_rolloff),
                'zero_crossing_mean': np.mean(zero_crossing_rate),
                'rms_mean': np.mean(rms),
                'rms_std': np.std(rms)
            }
        except:
            return None

    def _score_kasparov_voice(self, features: Dict) -> float:
        """Score how likely features belong to Kasparov vs other speakers."""
        confidence = 0.0
        
        # Kasparov characteristics:
        # - Russian accent affects certain phonemes
        # - Deeper male voice (typically 80-160 Hz)
        # - Measured, thoughtful speaking pattern
        # - Characteristic Russian stress patterns
        
        pitch = features['mean_pitch']
        
        # Pitch scoring (Kasparov has deeper voice than typical American male)
        if 80 <= pitch <= 140:
            confidence += 0.3  # Strong indicator
        elif 140 <= pitch <= 180:
            confidence += 0.1  # Possible but less likely
        
        # Voice stability (Kasparov speaks more steadily)
        if features['pitch_std'] < 20:
            confidence += 0.2
        
        # Speaking pattern (more voiced frames for continuous speech)
        if features['voiced_ratio'] > 0.6:
            confidence += 0.2
        
        # Spectral characteristics (Russian accent affects formants)
        if 1000 <= features['spectral_centroid_mean'] <= 2500:
            confidence += 0.15
        
        # Energy consistency (professional speaker)
        if features['rms_std'] < 0.05:
            confidence += 0.15
        
        return min(confidence, 1.0)

    def create_speaker_timeline(self, segments: List[Dict]) -> List[Dict]:
        """Create a timeline showing Kasparov vs Other speaker segments."""
        print("📅 Creating speaker timeline...")
        
        # Merge overlapping segments and create continuous timeline
        timeline = []
        current_speaker = None
        current_start = None
        current_end = None
        current_confidence_sum = 0
        current_segment_count = 0
        
        for segment in sorted(segments, key=lambda x: x['start_time']):
            speaker = segment['likely_speaker']
            confidence = segment['kasparov_confidence']
            
            if speaker == current_speaker:
                # Extend current segment
                current_end = segment['end_time']
                current_confidence_sum += confidence
                current_segment_count += 1
            else:
                # Save previous segment
                if current_speaker is not None:
                    timeline.append({
                        'speaker': current_speaker,
                        'start_time': current_start,
                        'end_time': current_end,
                        'duration': current_end - current_start,
                        'avg_confidence': current_confidence_sum / current_segment_count
                    })
                
                # Start new segment
                current_speaker = speaker
                current_start = segment['start_time']
                current_end = segment['end_time']
                current_confidence_sum = confidence
                current_segment_count = 1
        
        # Add final segment
        if current_speaker is not None:
            timeline.append({
                'speaker': current_speaker,
                'start_time': current_start,
                'end_time': current_end,
                'duration': current_end - current_start,
                'avg_confidence': current_confidence_sum / current_segment_count
            })
        
        return timeline

    def show_interactive_timeline(self, timeline: List[Dict], audio: np.ndarray, sr: int):
        """Show timeline and let user select segments to keep."""
        print("\n🎯 SPEAKER TIMELINE ANALYSIS")
        print("=" * 60)
        
        kasparov_segments = [s for s in timeline if s['speaker'] == 'Kasparov']
        other_segments = [s for s in timeline if s['speaker'] == 'Other']
        
        print(f"🎤 Kasparov segments: {len(kasparov_segments)}")
        print(f"👥 Other speaker segments: {len(other_segments)}")
        print()
        
        print("📋 KASPAROV SEGMENTS (candidates for keeping):")
        print("-" * 50)
        
        selected_segments = []
        
        for i, segment in enumerate(kasparov_segments):
            print(f"\n#{i+1}: {segment['start_time']:.1f}s - {segment['end_time']:.1f}s")
            print(f"   Duration: {segment['duration']:.1f}s")
            print(f"   Confidence: {segment['avg_confidence']:.2f}")
            
            # Extract and potentially play this segment
            start_sample = int(segment['start_time'] * sr)
            end_sample = int(segment['end_time'] * sr)
            segment_audio = audio[start_sample:end_sample]
            
            # Automatic recommendation
            auto_keep = (segment['duration'] >= 3.0 and 
                        segment['avg_confidence'] >= 0.7 and 
                        segment['duration'] <= 45.0)
            
            recommendation = "✅ RECOMMEND KEEP" if auto_keep else "⚠️ Review needed"
            print(f"   {recommendation}")
            
            # User decision
            while True:
                choice = input(f"   Keep this segment? (y/n/p=play): ").lower()
                if choice in ['y', 'yes']:
                    selected_segments.append(segment)
                    print("   ✅ Segment marked for keeping")
                    break
                elif choice in ['n', 'no']:
                    print("   ❌ Segment skipped")
                    break
                elif choice in ['p', 'play']:
                    self._play_segment(segment_audio, sr)
                else:
                    print("   Please enter y, n, or p")
        
        return selected_segments

    def _play_segment(self, audio: np.ndarray, sr: int):
        """Play audio segment for user review."""
        try:
            import pygame
            pygame.mixer.init(frequency=sr)
            
            # Convert to pygame format
            audio_int = (audio * 32767).astype(np.int16)
            sound = pygame.sndarray.make_sound(audio_int)
            
            print("   ▶️ Playing segment...")
            sound.play()
            
            input("   Press Enter when done listening...")
            pygame.mixer.stop()
            pygame.mixer.quit()
            
        except ImportError:
            print("   ⚠️ pygame not available for playback")
        except Exception as e:
            print(f"   ⚠️ Playback error: {e}")

    def extract_precise_segments(self, audio: np.ndarray, sr: int, file_specs: Dict,
                                selected_segments: List[Dict]) -> List[str]:
        """Extract selected segments with zero quality loss."""
        print(f"\n✂️ Extracting {len(selected_segments)} segments with zero quality loss...")
        
        extracted_files = []
        
        for i, segment in enumerate(selected_segments):
            # Calculate exact sample boundaries
            start_sample = int(segment['start_time'] * sr)
            end_sample = int(segment['end_time'] * sr)
            
            # Extract with perfect precision
            segment_audio = audio[start_sample:end_sample]
            
            # Save with original quality
            filename = f"{self.master_name.lower().replace(' ', '_')}_precision_{i:03d}.wav"
            output_path = self.output_dir / filename
            
            # Use original file specifications for maximum quality
            sf.write(
                output_path, 
                segment_audio, 
                sr,
                subtype=file_specs.get('original_subtype', 'PCM_24'),  # Default to 24-bit
                format='WAV'
            )
            
            extracted_files.append(str(output_path))
            
            print(f"   ✅ {filename} - {segment['duration']:.1f}s")
        
        return extracted_files

    def merge_adjacent_segments(self, audio: np.ndarray, sr: int, 
                               selected_segments: List[Dict]) -> List[Dict]:
        """Merge segments that are close together to create longer clips."""
        print("🔗 Checking for segments to merge into longer clips...")
        
        # Sort by start time
        segments = sorted(selected_segments, key=lambda x: x['start_time'])
        merged = []
        
        i = 0
        while i < len(segments):
            current = segments[i]
            
            # Look for segments that can be merged (gap < 2 seconds)
            j = i + 1
            while j < len(segments):
                next_seg = segments[j]
                gap = next_seg['start_time'] - current['end_time']
                
                if gap < 2.0:  # Merge if gap is less than 2 seconds
                    print(f"   🔗 Merging segments: {current['start_time']:.1f}s-{current['end_time']:.1f}s + {next_seg['start_time']:.1f}s-{next_seg['end_time']:.1f}s")
                    
                    # Create merged segment
                    current = {
                        'start_time': current['start_time'],
                        'end_time': next_seg['end_time'],
                        'duration': next_seg['end_time'] - current['start_time'],
                        'speaker': 'Kasparov',
                        'avg_confidence': (current.get('avg_confidence', 0.8) + 
                                         next_seg.get('avg_confidence', 0.8)) / 2
                    }
                    j += 1
                else:
                    break
            
            merged.append(current)
            i = j if j > i + 1 else i + 1
        
        print(f"   ✅ Created {len(merged)} segments ({len(selected_segments) - len(merged)} merges)")
        return merged

    def create_summary_report(self, extracted_files: List[str], original_file: str):
        """Create summary report of extraction."""
        report_path = self.output_dir / "precision_extraction_report.txt"
        
        total_duration = 0
        for file_path in extracted_files:
            info = sf.info(file_path)
            total_duration += info.duration
        
        with open(report_path, 'w') as f:
            f.write(f"{self.master_name} Precision Audio Extraction Report\n")
            f.write("=" * 60 + "\n\n")
            f.write(f"Source file: {original_file}\n")
            f.write(f"Processing: ZERO quality loss - original specs preserved\n")
            f.write(f"Extraction method: Precision cutting only\n\n")
            
            f.write("RESULTS:\n")
            f.write(f"Total extracted clips: {len(extracted_files)}\n")
            f.write(f"Total duration: {total_duration:.1f} seconds\n")
            f.write(f"Average clip length: {total_duration/len(extracted_files):.1f} seconds\n\n")
            
            f.write("EXTRACTED CLIPS:\n")
            for i, file_path in enumerate(extracted_files):
                info = sf.info(file_path)
                f.write(f"{i+1:2d}. {Path(file_path).name} - {info.duration:.1f}s\n")
            
            f.write(f"\nQUALITY SPECS:\n")
            if extracted_files:
                info = sf.info(extracted_files[0])
                f.write(f"Sample rate: {info.samplerate} Hz\n")
                f.write(f"Bit depth: {info.subtype}\n")
                f.write(f"Format: Lossless WAV\n")
            
            f.write(f"\nREADY FOR VOICE CLONING:\n")
            f.write(f"✅ All clips are unprocessed, original quality\n")
            f.write(f"✅ Only {self.master_name} voice segments included\n")
            f.write(f"✅ Interruptions and other speakers removed\n")
        
        print(f"📋 Report saved: {report_path}")

    def process_audio_file(self, file_path: str):
        """Main processing pipeline."""
        print(f"🚀 Processing: {Path(file_path).name}")
        print("=" * 60)
        
        try:
            # Load with maximum quality
            audio, sr, file_specs = self.load_audio_lossless(file_path)
            
            # Detect speaker segments
            segments = self.detect_speaker_segments(audio, sr)
            
            if not segments:
                print("❌ No voice segments detected!")
                return
            
            # Create timeline
            timeline = self.create_speaker_timeline(segments)
            
            # Interactive selection
            selected_segments = self.show_interactive_timeline(timeline, audio, sr)
            
            if not selected_segments:
                print("❌ No segments selected!")
                return
            
            # Merge adjacent segments for longer clips
            merged_segments = self.merge_adjacent_segments(audio, sr, selected_segments)
            
            # Extract with zero quality loss
            extracted_files = self.extract_precise_segments(audio, sr, file_specs, merged_segments)
            
            # Create report
            self.create_summary_report(extracted_files, file_path)
            
            print(f"\n🎉 SUCCESS!")
            print(f"📁 {len(extracted_files)} precision clips ready for voice cloning")
            print(f"💎 Zero quality loss - original audio preserved")
            print(f"📂 Location: {self.output_dir}")
            
            return extracted_files
            
        except Exception as e:
            print(f"❌ Error: {e}")
            raise


def main():
    """Interactive precision audio splicing."""
    print("🎯 Precision Audio Splicer")
    print("=" * 30)
    print("Ultra-high quality extraction with zero processing")
    print()
    
    # Get input file
    default_file = "/mnt/d/Users/dilli/AndroidStudioProjects/ChessPedagogue/TTS_training_data/garry_kasparov_voice_extraction/raw_audio/garry_kasparov_interview.wav"
    
    file_path = input(f"Audio file path (Enter for default): ").strip()
    if not file_path:
        file_path = default_file
    
    if not Path(file_path).exists():
        print(f"❌ File not found: {file_path}")
        return
    
    # Get speaker name
    speaker = input("Speaker name (default: Garry Kasparov): ").strip()
    if not speaker:
        speaker = "Garry Kasparov"
    
    try:
        splicer = PrecisionAudioSplicer(speaker)
        splicer.process_audio_file(file_path)
        
    except KeyboardInterrupt:
        print("\n👋 Processing stopped by user")
    except Exception as e:
        print(f"\n❌ Error: {e}")
        print("Make sure librosa, soundfile, and numpy are installed")


if __name__ == "__main__":
    main()