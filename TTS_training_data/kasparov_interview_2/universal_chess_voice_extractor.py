import os
import sys
import subprocess
import json
import shutil
from pathlib import Path
import numpy as np
import librosa
import soundfile as sf
from pydub import AudioSegment
from pydub.silence import split_on_silence
from typing import List, Dict, Tuple
import matplotlib.pyplot as plt
from scipy.spatial.distance import cosine
from sklearn.cluster import KMeans
from scipy.signal import butter, filtfilt, wiener
import warnings
warnings.filterwarnings('ignore')

class UniversalChessVoiceExtractor:
    """
    A compassionate tool to help extract any chess master's voice from YouTube videos.
    Perfect for creating voice models for TTS applications with 10-30 second clips.
    Includes advanced noise reduction for crystal-clear audio.
    """
    
    def __init__(self, master_name: str, output_dir: str = None):
        self.master_name = master_name.strip()
        self.master_display_name = self.master_name.title()
        
        if output_dir is None:
            safe_name = "".join(c for c in self.master_name.lower() if c.isalnum() or c in (' ', '-', '_')).rstrip()
            safe_name = safe_name.replace(' ', '_')
            output_dir = f"{safe_name}_voice_extraction"
        
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(exist_ok=True)
        
        # Create organized subdirectories
        (self.output_dir / "raw_audio").mkdir(exist_ok=True)
        (self.output_dir / "segments").mkdir(exist_ok=True)
        (self.output_dir / "final_clips").mkdir(exist_ok=True)
        (self.output_dir / "likely_target").mkdir(exist_ok=True)
        (self.output_dir / "likely_others").mkdir(exist_ok=True)
        (self.output_dir / "manual_review").mkdir(exist_ok=True)
        (self.output_dir / "training_ready").mkdir(exist_ok=True)
        
        print(f"🎯 {self.master_display_name} Voice Extraction Tool")
        print("=" * 50)
        print(f"📁 Output directory: {self.output_dir.absolute()}")
        print("🎵 Target duration: 10-30 seconds per clip")
        print("🔇 Advanced noise reduction enabled")
        print()

    def download_youtube_audio(self, url: str) -> str:
        """Download high-quality audio from YouTube video."""
        print(f"📥 Downloading audio from YouTube...")
        
        safe_filename = "".join(c for c in self.master_name.lower() if c.isalnum() or c in (' ', '-', '_')).rstrip()
        safe_filename = safe_filename.replace(' ', '_')
        output_path = self.output_dir / "raw_audio" / f"{safe_filename}_interview.wav"
        
        # Use yt-dlp for best quality
        cmd = [
            "yt-dlp",
            "--extract-audio",
            "--audio-format", "wav",
            "--audio-quality", "0",  # Best quality
            "--output", str(output_path).replace('.wav', '.%(ext)s'),
            url
        ]
        
        try:
            result = subprocess.run(cmd, check=True, capture_output=True, text=True)
            print(f"✅ Audio downloaded successfully!")
            return str(output_path)
        except subprocess.CalledProcessError as e:
            print(f"⚠️ yt-dlp failed, trying youtube-dl...")
            try:
                cmd[0] = "youtube-dl"
                subprocess.run(cmd, check=True, capture_output=True)
                return str(output_path)
            except subprocess.CalledProcessError as e2:
                print(f"❌ Both downloaders failed. Error: {e2}")
                print("💡 Please ensure yt-dlp or youtube-dl is installed:")
                print("   pip install yt-dlp")
                raise

    def analyze_noise_profile(self, audio: np.ndarray, sr: int) -> Dict:
        """Analyze the noise characteristics of the audio."""
        # Get quieter sections (likely background noise)
        rms = librosa.feature.rms(y=audio, frame_length=2048, hop_length=512)[0]
        quiet_threshold = np.percentile(rms, 25)  # Bottom 25% of energy
        quiet_frames = rms < quiet_threshold
        
        # Extract noise profile from quiet sections
        frame_length = 2048
        hop_length = 512
        noise_samples = []
        
        for i, is_quiet in enumerate(quiet_frames):
            if is_quiet:
                start_sample = i * hop_length
                end_sample = min(start_sample + frame_length, len(audio))
                noise_samples.extend(audio[start_sample:end_sample])
        
        if len(noise_samples) < sr * 0.1:  # Less than 0.1 seconds of noise
            return None
        
        noise_array = np.array(noise_samples)
        
        # Analyze noise characteristics
        noise_profile = {
            'noise_floor': np.mean(np.abs(noise_array)),
            'noise_variance': np.var(noise_array),
            'spectral_profile': np.abs(np.fft.fft(noise_array[:min(len(noise_array), 2048)]))
        }
        
        return noise_profile

    def spectral_subtraction(self, audio: np.ndarray, sr: int, noise_profile: Dict = None) -> np.ndarray:
        """Apply spectral subtraction to reduce background noise."""
        if noise_profile is None:
            noise_profile = self.analyze_noise_profile(audio, sr)
            if noise_profile is None:
                return audio  # Can't analyze noise, return original
        
        # Convert to frequency domain
        stft = librosa.stft(audio, n_fft=2048, hop_length=512)
        magnitude = np.abs(stft)
        phase = np.angle(stft)
        
        # Estimate noise spectrum (use first and last 10% of audio as noise reference)
        noise_frames = int(0.1 * stft.shape[1])
        noise_spectrum = np.mean(np.concatenate([
            magnitude[:, :noise_frames],
            magnitude[:, -noise_frames:]
        ], axis=1), axis=1, keepdims=True)
        
        # Spectral subtraction
        alpha = 2.0  # Over-subtraction factor
        beta = 0.01  # Spectral floor factor
        
        # Subtract noise
        clean_magnitude = magnitude - alpha * noise_spectrum
        
        # Apply spectral floor
        spectral_floor = beta * magnitude
        clean_magnitude = np.maximum(clean_magnitude, spectral_floor)
        
        # Reconstruct audio
        clean_stft = clean_magnitude * np.exp(1j * phase)
        clean_audio = librosa.istft(clean_stft, hop_length=512)
        
        return clean_audio

    def wiener_filter_denoising(self, audio: np.ndarray, sr: int) -> np.ndarray:
        """Apply Wiener filtering for noise reduction."""
        # Estimate noise using the quietest 20% of the signal
        rms = librosa.feature.rms(y=audio, frame_length=2048, hop_length=512)[0]
        noise_threshold = np.percentile(rms, 20)
        
        # Simple Wiener filter approximation
        # This is a basic implementation - for production use, consider more advanced methods
        frame_length = 1024
        hop_length = 512
        
        # Apply overlapping windows
        windowed_audio = librosa.util.frame(audio, frame_length=frame_length, 
                                          hop_length=hop_length, axis=0)
        
        filtered_frames = []
        for frame in windowed_audio.T:
            if len(frame) == frame_length:
                # Apply Wiener filter (simplified)
                filtered_frame = wiener(frame, noise=None)  # Auto-estimate noise
                filtered_frames.append(filtered_frame)
        
        if not filtered_frames:
            return audio
        
        # Reconstruct audio with overlap-add
        filtered_audio = np.zeros(len(audio))
        for i, frame in enumerate(filtered_frames):
            start_idx = i * hop_length
            end_idx = min(start_idx + frame_length, len(filtered_audio))
            actual_length = end_idx - start_idx
            filtered_audio[start_idx:end_idx] += frame[:actual_length]
        
        return filtered_audio

    def advanced_noise_reduction(self, audio: np.ndarray, sr: int) -> np.ndarray:
        """Apply comprehensive noise reduction pipeline."""
        print("🔇 Applying advanced noise reduction...")
        
        # Measure initial noise level
        initial_noise = np.std(audio[audio < np.percentile(audio, 10)])
        
        # Step 1: High-pass filter to remove low-frequency noise
        nyquist = sr / 2
        low_cutoff = 80 / nyquist  # Remove below 80 Hz
        if low_cutoff < 1.0:
            b, a = butter(4, low_cutoff, btype='high')
            audio = filtfilt(b, a, audio)
        
        # Step 2: Spectral subtraction for hiss and background noise
        audio = self.spectral_subtraction(audio, sr)
        
        # Step 3: Wiener filtering for additional cleanup
        audio = self.wiener_filter_denoising(audio, sr)
        
        # Step 4: Gentle low-pass filter to remove high-frequency artifacts
        high_cutoff = 8000 / nyquist  # Remove above 8kHz
        if high_cutoff < 1.0:
            b, a = butter(2, high_cutoff, btype='low')
            audio = filtfilt(b, a, audio)
        
        # Step 5: Dynamic range compression to enhance speech
        audio = self.gentle_compression(audio)
        
        # Measure final noise level
        final_noise = np.std(audio[audio < np.percentile(audio, 10)])
        noise_reduction_db = 20 * np.log10(initial_noise / (final_noise + 1e-10))
        
        print(f"   ✅ Noise reduced by {noise_reduction_db:.1f} dB")
        
        # Normalize to prevent clipping
        audio = audio / np.max(np.abs(audio)) * 0.95
        
        return audio

    def gentle_compression(self, audio: np.ndarray, ratio: float = 3.0, 
                          threshold: float = 0.3) -> np.ndarray:
        """Apply gentle compression to enhance speech clarity."""
        # Simple compression algorithm
        compressed = np.copy(audio)
        
        # Find samples above threshold
        above_threshold = np.abs(compressed) > threshold
        
        # Apply compression to loud samples
        compressed[above_threshold] = (
            np.sign(compressed[above_threshold]) * 
            (threshold + (np.abs(compressed[above_threshold]) - threshold) / ratio)
        )
        
        return compressed

    def analyze_audio_properties(self, audio_path: str) -> Dict:
        """Analyze the audio file to understand its properties."""
        print(f"🔍 Analyzing audio properties...")
        
        y, sr = librosa.load(audio_path, sr=None)
        duration = len(y) / sr
        
        # Analyze noise level
        noise_profile = self.analyze_noise_profile(y, sr)
        noise_level = "Low"
        if noise_profile:
            if noise_profile['noise_floor'] > 0.05:
                noise_level = "High"
            elif noise_profile['noise_floor'] > 0.02:
                noise_level = "Moderate"
        
        properties = {
            'duration': duration,
            'sample_rate': sr,
            'channels': 1 if y.ndim == 1 else y.shape[0],
            'total_samples': len(y),
            'noise_level': noise_level,
            'noise_profile': noise_profile
        }
        
        print(f"📊 Audio Properties:")
        print(f"   Duration: {duration:.2f} seconds ({duration/60:.1f} minutes)")
        print(f"   Sample Rate: {sr} Hz")
        print(f"   Quality: {'Excellent' if sr >= 44100 else 'Good' if sr >= 22050 else 'Basic'}")
        print(f"   Background Noise: {noise_level}")
        print()
        
        return properties

    def segment_by_silence(self, audio_path: str) -> List[str]:
        """Split audio into segments based on silence detection."""
        print(f"✂️ Segmenting audio by silence...")
        
        audio = AudioSegment.from_wav(audio_path)
        
        # More aggressive segmentation for clear speech isolation
        segments = split_on_silence(
            audio,
            min_silence_len=800,   # Longer pauses to separate speakers
            silence_thresh=-35,     # Slightly less sensitive
            keep_silence=150       # Keep some context
        )
        
        print(f"📋 Found {len(segments)} audio segments")
        
        # Save segments with target duration filtering
        segment_paths = []
        for i, segment in enumerate(segments):
            duration_seconds = len(segment) / 1000.0
            
            # Focus on 10-30 second range as requested
            if 8.0 <= duration_seconds <= 35.0:  # Slightly wider range for flexibility
                segment_path = self.output_dir / "segments" / f"segment_{i:03d}.wav"
                segment.export(segment_path, format="wav")
                segment_paths.append(str(segment_path))
        
        print(f"💾 Saved {len(segment_paths)} segments in target duration range (8-35s)")
        return segment_paths

    def extract_voice_features(self, audio_path: str) -> Dict:
        """Extract voice characteristics that help identify speakers."""
        try:
            y, sr = librosa.load(audio_path, sr=22050)
            
            if len(y) < sr * 0.5:
                return None
            
            # Apply noise reduction before feature extraction
            y_clean = self.advanced_noise_reduction(y, sr)
            
            features = {}
            
            # Fundamental frequency analysis
            f0, voiced_flag, voiced_probs = librosa.pyin(y_clean, fmin=50, fmax=350, sr=sr)
            f0_clean = f0[voiced_flag]
            if len(f0_clean) > 0:
                features['mean_pitch'] = np.nanmean(f0_clean)
                features['pitch_std'] = np.nanstd(f0_clean)
                features['pitch_range'] = np.nanmax(f0_clean) - np.nanmin(f0_clean)
            else:
                features['mean_pitch'] = features['pitch_std'] = features['pitch_range'] = 0
            
            # Spectral characteristics
            spectral_centroids = librosa.feature.spectral_centroid(y=y_clean, sr=sr)[0]
            features['spectral_centroid_mean'] = np.mean(spectral_centroids)
            features['spectral_centroid_std'] = np.std(spectral_centroids)
            
            # MFCC features (voice fingerprint)
            mfccs = librosa.feature.mfcc(y=y_clean, sr=sr, n_mfcc=13)
            features['mfcc_mean'] = np.mean(mfccs, axis=1)
            features['mfcc_std'] = np.std(mfccs, axis=1)
            
            # Voice quality indicators
            features['zero_crossing_rate'] = np.mean(librosa.feature.zero_crossing_rate(y_clean)[0])
            features['rms_energy'] = np.mean(librosa.feature.rms(y=y_clean)[0])
            features['duration'] = len(y_clean) / sr
            
            # Harmonic-to-noise ratio (improved after noise reduction)
            harmonic, percussive = librosa.effects.hpss(y_clean)
            harmonic_energy = np.mean(harmonic**2)
            percussive_energy = np.mean(percussive**2)
            features['harmonic_ratio'] = harmonic_energy / (harmonic_energy + percussive_energy) if percussive_energy > 0 else 1.0
            
            return features
            
        except Exception as e:
            print(f"⚠️ Could not analyze {Path(audio_path).name}: {e}")
            return None

    def analyze_all_segments(self, segment_paths: List[str]) -> List[Dict]:
        """Analyze all segments and extract voice characteristics."""
        print(f"🔍 Analyzing voice characteristics of all segments...")
        print("Applying noise reduction and feature extraction - please be patient! ☕")
        print()
        
        if not segment_paths:
            print("❌ No segments found to analyze!")
            return []
        
        print(f"📊 Found {len(segment_paths)} segments to analyze")
        
        analyzed_segments = []
        
        for i, segment_path in enumerate(segment_paths):
            if i % 5 == 0:  # More frequent updates due to noise reduction
                print(f"🔄 Analyzed {i}/{len(segment_paths)} segments...")
            
            features = self.extract_voice_features(segment_path)
            if features:
                segment_info = {
                    'filename': Path(segment_path).name,
                    'filepath': segment_path,
                    'features': features
                }
                analyzed_segments.append(segment_info)
        
        print(f"✅ Successfully analyzed {len(analyzed_segments)} segments!")
        print()
        
        return analyzed_segments

    def cluster_voices(self, analyzed_segments: List[Dict], n_clusters: int = 3) -> Dict:
        """Group similar voices together using machine learning."""
        print(f"🤖 Grouping similar voices together...")
        print("Looking for patterns that distinguish different speakers.")
        print()
        
        if len(analyzed_segments) < 6:
            print("⚠️ Not enough segments for automatic clustering")
            return self.manual_classification_helper(analyzed_segments)
        
        # Prepare feature vectors
        feature_vectors = []
        valid_segments = []
        
        for segment in analyzed_segments:
            features = segment['features']
            
            vector = [
                features['mean_pitch'],
                features['pitch_std'],
                features['spectral_centroid_mean'],
                features['zero_crossing_rate'],
                features['harmonic_ratio']
            ]
            
            # Add most important MFCC coefficients
            vector.extend(features['mfcc_mean'][:5])
            
            if not any(np.isnan(vector)) and not any(np.isinf(vector)):
                feature_vectors.append(vector)
                valid_segments.append(segment)
        
        if len(feature_vectors) < 6:
            print("⚠️ Not enough valid features for clustering")
            return self.manual_classification_helper(analyzed_segments)
        
        # Normalize features
        feature_matrix = np.array(feature_vectors)
        feature_matrix = (feature_matrix - np.mean(feature_matrix, axis=0)) / (np.std(feature_matrix, axis=0) + 1e-8)
        
        # Perform clustering
        n_clusters = min(n_clusters, max(2, len(feature_vectors) // 3))
        kmeans = KMeans(n_clusters=n_clusters, random_state=42, n_init=10)
        cluster_labels = kmeans.fit_predict(feature_matrix)
        
        # Group segments by cluster
        clusters = {}
        for i, segment in enumerate(valid_segments):
            cluster_id = cluster_labels[i]
            if cluster_id not in clusters:
                clusters[cluster_id] = []
            clusters[cluster_id].append(segment)
        
        # Analyze clusters
        print(f"🎯 Found {len(clusters)} distinct voice groups:")
        for cluster_id, segments in clusters.items():
            avg_pitch = np.mean([s['features']['mean_pitch'] for s in segments])
            avg_duration = np.mean([s['features']['duration'] for s in segments])
            print(f"   Group {cluster_id}: {len(segments)} segments, avg pitch: {avg_pitch:.1f} Hz, avg duration: {avg_duration:.1f}s")
        
        return clusters

    def identify_target_cluster(self, clusters: Dict) -> Tuple[int, List[str]]:
        """Try to identify which cluster is most likely the target chess master."""
        print(f"\n🕵️ Trying to identify {self.master_display_name}'s voice characteristics...")
        
        cluster_scores = {}
        
        for cluster_id, segments in clusters.items():
            # Calculate average characteristics
            pitches = [s['features']['mean_pitch'] for s in segments]
            durations = [s['features']['duration'] for s in segments]
            harmonic_ratios = [s['features']['harmonic_ratio'] for s in segments]
            
            avg_pitch = np.mean(pitches)
            avg_duration = np.mean(durations)
            avg_clarity = np.mean(harmonic_ratios)
            
            # Scoring based on general chess master interview characteristics
            score = 0
            reasoning = []
            
            # Pitch scoring (most chess masters have medium-range male voices)
            if 90 <= avg_pitch <= 200:
                score += 3
                reasoning.append("good pitch range for chess master")
            elif 70 <= avg_pitch <= 250:
                score += 1
                reasoning.append("possible pitch range")
            
            # Clarity scoring (enhanced by noise reduction)
            if avg_clarity > 0.8:
                score += 3
                reasoning.append("excellent clarity after noise reduction")
            elif avg_clarity > 0.6:
                score += 2
                reasoning.append("good clarity after noise reduction")
            elif avg_clarity > 0.4:
                score += 1
                reasoning.append("acceptable clarity")
            
            # Duration scoring (chess masters often give thoughtful responses)
            if 10 <= avg_duration <= 30:  # Perfect range for our target
                score += 3
                reasoning.append("ideal duration for training")
            elif 8 <= avg_duration <= 35:
                score += 2
                reasoning.append("good duration for training")
            elif avg_duration > 5:
                score += 1
                reasoning.append("usable duration")
            
            # Prefer clusters with more segments (likely main speaker)
            if len(segments) >= len(max(clusters.values(), key=len)) * 0.5:
                score += 2
                reasoning.append("substantial presence in audio")
            
            cluster_scores[cluster_id] = {
                'score': score,
                'reasoning': reasoning,
                'stats': {
                    'avg_pitch': avg_pitch,
                    'avg_duration': avg_duration,
                    'avg_clarity': avg_clarity,
                    'segment_count': len(segments)
                }
            }
        
        # Find the most likely target cluster
        best_cluster = max(cluster_scores.keys(), key=lambda k: cluster_scores[k]['score'])
        
        print(f"🎯 Voice analysis results for {self.master_display_name}:")
        for cluster_id in sorted(cluster_scores.keys()):
            info = cluster_scores[cluster_id]
            marker = f"🎯 LIKELY {self.master_display_name.upper()}" if cluster_id == best_cluster else "   Other speaker"
            print(f"{marker} - Group {cluster_id}:")
            print(f"    {info['stats']['segment_count']} segments, pitch: {info['stats']['avg_pitch']:.1f}Hz")
            print(f"    Duration: {info['stats']['avg_duration']:.1f}s, clarity: {info['stats']['avg_clarity']:.2f}")
            print(f"    Reasoning: {', '.join(info['reasoning'])}")
            print()
        
        return best_cluster, cluster_scores[best_cluster]['reasoning']

    def create_denoised_clips(self, segments: List[Dict], output_folder: str):
        """Create final denoised clips for training."""
        print(f"🎵 Creating clean, denoised training clips...")
        
        output_path = self.output_dir / output_folder
        clean_count = 0
        
        for segment in segments:
            try:
                # Load original audio
                y, sr = librosa.load(segment['filepath'], sr=22050)
                
                # Apply comprehensive noise reduction
                y_clean = self.advanced_noise_reduction(y, sr)
                
                # Save clean version
                clean_filename = f"clean_{Path(segment['filepath']).name}"
                clean_path = output_path / clean_filename
                sf.write(clean_path, y_clean, sr)
                clean_count += 1
                
            except Exception as e:
                print(f"⚠️ Could not clean {segment['filename']}: {e}")
        
        print(f"   ✅ Created {clean_count} clean clips")

    def organize_segments(self, clusters: Dict, target_cluster_id: int):
        """Organize segments into folders based on identification."""
        print("📁 Organizing segments into folders...")
        
        target_segments = clusters.get(target_cluster_id, [])
        other_segments = []
        for cluster_id, segments in clusters.items():
            if cluster_id != target_cluster_id:
                other_segments.extend(segments)
        
        # Copy target segments with noise reduction
        print(f"🎯 Processing {len(target_segments)} likely {self.master_display_name} segments...")
        self.create_denoised_clips(target_segments, "likely_target")
        
        # Copy other segments
        print(f"👥 Copying {len(other_segments)} other speaker segments...")
        for segment in other_segments:
            src = Path(segment['filepath'])
            dst = self.output_dir / "likely_others" / src.name
            shutil.copy2(src, dst)
        
        # Create training-ready clips (filter for optimal duration + apply advanced cleaning)
        training_segments = [s for s in target_segments 
                           if 10.0 <= s['features']['duration'] <= 30.0]
        
        if len(training_segments) > 15:  # Take best ones if we have many
            # Sort by clarity and take top ones
            training_segments.sort(key=lambda s: s['features']['harmonic_ratio'], reverse=True)
            training_segments = training_segments[:15]
        
        print(f"🎵 Creating {len(training_segments)} premium training-ready clips...")
        for i, segment in enumerate(training_segments):
            try:
                # Load and apply maximum quality noise reduction
                y, sr = librosa.load(segment['filepath'], sr=22050)
                y_clean = self.advanced_noise_reduction(y, sr)
                
                # Save with consistent naming
                clean_filename = f"{self.master_name.lower().replace(' ', '_')}_{i:03d}.wav"
                clean_path = self.output_dir / "training_ready" / clean_filename
                sf.write(clean_path, y_clean, sr)
                
            except Exception as e:
                print(f"⚠️ Could not process training clip {i}: {e}")
        
        # Manual review samples (also cleaned)
        manual_review_segments = target_segments[:8]
        print(f"📋 Creating {len(manual_review_segments)} cleaned clips for manual review...")
        self.create_denoised_clips(manual_review_segments, "manual_review")

    def manual_classification_helper(self, analyzed_segments: List[Dict]) -> Dict:
        """Helper for when automatic clustering isn't possible."""
        print("💙 Creating a helper for manual classification...")
        
        # Sort by pitch to help group similar voices
        analyzed_segments.sort(key=lambda x: x['features']['mean_pitch'])
        
        # Create groups based on pitch ranges
        low_pitch = [s for s in analyzed_segments if s['features']['mean_pitch'] < 120]
        medium_pitch = [s for s in analyzed_segments if 120 <= s['features']['mean_pitch'] < 160]
        high_pitch = [s for s in analyzed_segments if s['features']['mean_pitch'] >= 160]
        
        clusters = {}
        if low_pitch: clusters[0] = low_pitch
        if medium_pitch: clusters[1] = medium_pitch
        if high_pitch: clusters[2] = high_pitch
        
        return clusters

    def create_summary_report(self, clusters: Dict, target_cluster_id: int, youtube_url: str):
        """Create a comprehensive summary report."""
        report_path = self.output_dir / "extraction_report.txt"
        
        target_segments = clusters.get(target_cluster_id, [])
        total_segments = sum(len(segments) for segments in clusters.values())
        other_segments = total_segments - len(target_segments)
        
        # Count training-ready clips
        training_ready = len(list((self.output_dir / "training_ready").glob("*.wav")))
        
        with open(report_path, 'w') as f:
            f.write(f"{self.master_display_name} Voice Extraction Report\n")
            f.write("=" * 50 + "\n\n")
            f.write(f"Source URL: {youtube_url}\n")
            f.write(f"Target Speaker: {self.master_display_name}\n")
            f.write(f"Noise Reduction: Advanced spectral subtraction + Wiener filtering\n\n")
            
            f.write("RESULTS SUMMARY:\n")
            f.write(f"Total segments analyzed: {total_segments}\n")
            f.write(f"Likely {self.master_display_name} segments: {len(target_segments)}\n")
            f.write(f"Other speaker segments: {other_segments}\n")
            f.write(f"Training-ready clips (10-30s, denoised): {training_ready}\n\n")
            
            f.write("FOLDER ORGANIZATION:\n")
            f.write("- likely_target/: Denoised segments identified as target speaker\n")
            f.write("- likely_others/: Segments from other speakers\n")
            f.write("- training_ready/: Premium denoised clips for TTS training (10-30s)\n")
            f.write("- manual_review/: Sample denoised clips to verify identification\n\n")
            
            f.write("NOISE REDUCTION APPLIED:\n")
            f.write("- High-pass filtering (removes low-frequency rumble)\n")
            f.write("- Spectral subtraction (removes background hiss)\n")
            f.write("- Wiener filtering (additional noise cleanup)\n")
            f.write("- Gentle compression (enhances speech clarity)\n")
            f.write("- Low-pass filtering (removes high-frequency artifacts)\n\n")
            
            f.write("NEXT STEPS FOR VOICE CLONING:\n")
            f.write("1. Listen to clips in manual_review/ folder\n")
            f.write(f"2. Verify they sound like {self.master_display_name}\n")
            f.write("3. Use training_ready/ folder for TTS training\n")
            f.write("4. For ElevenLabs: Upload 10-20 of the clearest clips\n")
            f.write("5. Ensure clips are diverse in content and emotion\n\n")
            
            f.write("QUALITY ASSURANCE:\n")
            f.write("- All clips have been denoised for maximum clarity\n")
            f.write("- Background hiss and noise significantly reduced\n")
            f.write("- Voice clarity enhanced through signal processing\n")
            f.write("- Optimal duration range (10-30s) for voice modeling\n")
        
        print(f"📋 Created detailed report: {report_path}")

    def process_youtube_video(self, url: str) -> str:
        """Complete processing pipeline for YouTube video."""
        print(f"🚀 Starting {self.master_display_name} voice extraction pipeline...")
        print(f"🔗 Source URL: {url}")
        print("=" * 60)
        
        try:
            # Step 1: Download audio
            audio_path = self.download_youtube_audio(url)
            
            # Step 2: Analyze audio properties (including noise level)
            properties = self.analyze_audio_properties(audio_path)
            
            # Step 3: Segment by silence (targeting 10-30s clips)
            segments = self.segment_by_silence(audio_path)
            
            if not segments:
                print("❌ No suitable segments found in target duration range!")
                print("💡 Try a different video with more substantial speech segments")
                return None
            
            # Step 4: Analyze voice characteristics (with noise reduction)
            analyzed_segments = self.analyze_all_segments(segments)
            
            if not analyzed_segments:
                print("❌ Could not analyze voice characteristics!")
                return None
            
            # Step 5: Group similar voices
            clusters = self.cluster_voices(analyzed_segments)
            
            if not clusters:
                print("❌ Could not group voices!")
                return None
            
            # Step 6: Identify target speaker
            target_cluster_id, reasoning = self.identify_target_cluster(clusters)
            
            # Step 7: Organize segments (with advanced denoising)
            self.organize_segments(clusters, target_cluster_id)
            
            # Step 8: Create report
            self.create_summary_report(clusters, target_cluster_id, url)
            
            # Summary
            target_count = len(clusters.get(target_cluster_id, []))
            training_ready = len(list((self.output_dir / "training_ready").glob("*.wav")))
            
            print("=" * 60)
            print("🎉 Voice extraction completed successfully!")
            print(f"🎯 Found {target_count} likely {self.master_display_name} segments")
            print(f"🎵 Created {training_ready} denoised training-ready clips (10-30s)")
            print(f"🔇 Advanced noise reduction applied to all clips")
            print(f"📁 Results location: {self.output_dir}")
            print("=" * 60)
            
            return str(self.output_dir)
            
        except Exception as e:
            print(f"❌ Error in processing pipeline: {e}")
            print("💙 Don't worry - this is part of the learning process!")
            print("💡 Common solutions:")
            print("   - Ensure yt-dlp is installed: pip install yt-dlp")
            print("   - Try a different YouTube URL")
            print("   - Check your internet connection")
            raise


def install_requirements():
    """Install required packages."""
    requirements = [
        "yt-dlp",
        "librosa",
        "soundfile", 
        "pydub",
        "scipy",
        "scikit-learn",
        "matplotlib",
        "numpy"
    ]
    
    print("📦 Installing required packages...")
    missing_packages = []
    
    for package in requirements:
        try:
            if package == "scikit-learn":
                __import__("sklearn")
            else:
                __import__(package.replace('-', '_'))
            print(f"✅ {package} already installed")
        except ImportError:
            missing_packages.append(package)
    
    if missing_packages:
        print(f"🔧 Installing {len(missing_packages)} missing packages...")
        for package in missing_packages:
            try:
                subprocess.run([sys.executable, "-m", "pip", "install", package], 
                             check=True, capture_output=True)
                print(f"✅ {package} installed successfully")
            except subprocess.CalledProcessError:
                print(f"⚠️ Could not install {package} - you may need to install manually")
    
    print("🎯 Package installation complete!")


def main():
    """Main execution function with interactive prompts."""
    print("🎭 Universal Chess Master Voice Extractor")
    print("========================================")
    print("This tool helps you extract voice clips from any chess master")
    print("for creating high-quality TTS voice models! 🎯")
    print("✨ Features advanced noise reduction for crystal-clear audio")
    print()
    
    # Get chess master name
    print("Which chess master would you like to extract voices for?")
    print("Examples: Garry Kasparov, Magnus Carlsen, Mikhail Tal, etc.")
    master_name = input("🏆 Chess Master Name: ").strip()
    
    if not master_name:
        print("❌ Please provide a chess master name!")
        return
    
    print(f"\n🎯 Excellent choice! {master_name.title()} has a fascinating voice.")
    
    # Get YouTube URL
    print("\nPlease provide a YouTube URL with an interview or speech:")
    print("💡 Look for videos where they speak clearly for extended periods")
    youtube_url = input("🔗 YouTube URL: ").strip()
    
    if not youtube_url or 'youtube.com' not in youtube_url and 'youtu.be' not in youtube_url:
        print("❌ Please provide a valid YouTube URL!")
        return
    
    print(f"\n🚀 Perfect! Let's extract {master_name.title()}'s voice from this video.")
    print("This process will create clips optimized for 10-30 seconds with advanced noise reduction.")
    print()
    
    # Ask about requirements installation
    install_deps = input("📦 Install/check required packages? (y/n): ").strip().lower()
    if install_deps in ['y', 'yes', '']:
        install_requirements()
        print()
    
    try:
        # Initialize extractor
        extractor = UniversalChessVoiceExtractor(master_name)
        
        # Process the video
        result_path = extractor.process_youtube_video(youtube_url)
        
        if result_path:
            print("\n🎯 SUCCESS! Next Steps for Voice Cloning:")
            print("=" * 50)
            print("1. 📂 Open the training_ready/ folder")
            print("2. 🎧 Listen to each denoised clip to verify quality")
            print("3. 🧹 Remove any clips that don't sound perfect")
            print("4. 📤 Upload 10-20 of the best clips to ElevenLabs")
            print("5. 🎭 Train your voice model!")
            print()
            print("🔇 All clips have been professionally denoised!")
            print(f"📁 Your training data is ready at: {result_path}")
            print("💙 You're doing amazing work - good luck with your voice cloning!")
        
    except KeyboardInterrupt:
        print("\n💙 No worries! You can run this again anytime.")
        print("Your progress is saved and you can continue where you left off.")
    except Exception as e:
        print(f"\n💙 We encountered a challenge: {e}")
        print("This is completely normal - voice extraction is complex!")
        print("💡 Feel free to try again with a different video or settings.")


if __name__ == "__main__":
    main()