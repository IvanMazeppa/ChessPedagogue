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
from typing import List, Dict, Tuple, Optional
import matplotlib.pyplot as plt
from scipy.spatial.distance import cosine
from sklearn.cluster import KMeans
from scipy.signal import butter, filtfilt
import warnings
warnings.filterwarnings('ignore')

class IntelligentVoiceExtractor:
    """
    A smart voice extractor that preserves audio quality and provides interactive control.
    Only applies processing when needed, and lets you test samples before full processing.
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
        (self.output_dir / "test_samples").mkdir(exist_ok=True)
        (self.output_dir / "likely_target").mkdir(exist_ok=True)
        (self.output_dir / "likely_others").mkdir(exist_ok=True)
        (self.output_dir / "manual_review").mkdir(exist_ok=True)
        (self.output_dir / "training_ready").mkdir(exist_ok=True)
        
        # Processing settings determined by quality analysis
        self.processing_mode = "preserve"  # "preserve", "light", "aggressive"
        self.original_sample_rate = None
        self.audio_quality_score = None
        
        print(f"🎯 {self.master_display_name} Intelligent Voice Extractor")
        print("=" * 50)
        print(f"📁 Output directory: {self.output_dir.absolute()}")
        print("🎵 Target duration: 10-30 seconds per clip")
        print("🧠 Intelligent quality-preserving processing")
        print("🎧 Interactive test sample generation")
        print()

    def download_youtube_audio(self, url: str) -> str:
        """Download highest quality audio from YouTube video."""
        print(f"📥 Downloading highest quality audio from YouTube...")
        
        safe_filename = "".join(c for c in self.master_name.lower() if c.isalnum() or c in (' ', '-', '_')).rstrip()
        safe_filename = safe_filename.replace(' ', '_')
        output_path = self.output_dir / "raw_audio" / f"{safe_filename}_interview.wav"
        
        # Use yt-dlp with maximum quality settings
        cmd = [
            "yt-dlp",
            "--extract-audio",
            "--audio-format", "wav",
            "--audio-quality", "0",  # Best quality available
            "--prefer-free-formats",
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

    def analyze_audio_quality(self, audio_path: str) -> Dict:
        """Intelligently analyze audio quality to determine processing needs."""
        print(f"🔍 Analyzing audio quality to determine optimal processing...")
        
        # Load with original sample rate preserved
        y, sr = librosa.load(audio_path, sr=None)
        self.original_sample_rate = sr
        duration = len(y) / sr
        
        # Comprehensive quality analysis
        quality_metrics = {}
        
        # 1. Sample rate quality
        if sr >= 48000:
            quality_metrics['sample_rate_score'] = 10
            quality_metrics['sample_rate_desc'] = "Excellent (Studio quality)"
        elif sr >= 44100:
            quality_metrics['sample_rate_score'] = 9
            quality_metrics['sample_rate_desc'] = "Very High (CD quality)"
        elif sr >= 22050:
            quality_metrics['sample_rate_score'] = 7
            quality_metrics['sample_rate_desc'] = "Good"
        else:
            quality_metrics['sample_rate_score'] = 5
            quality_metrics['sample_rate_desc'] = "Basic"
        
        # 2. Dynamic range analysis
        dynamic_range = np.max(y) - np.min(y)
        if dynamic_range > 1.8:
            quality_metrics['dynamic_range_score'] = 10
        elif dynamic_range > 1.0:
            quality_metrics['dynamic_range_score'] = 8
        else:
            quality_metrics['dynamic_range_score'] = 6
        
        # 3. Noise floor analysis
        rms = librosa.feature.rms(y=y, frame_length=2048, hop_length=512)[0]
        noise_floor = np.percentile(rms, 10)  # Bottom 10% as noise estimate
        
        if noise_floor < 0.005:
            quality_metrics['noise_score'] = 10
            quality_metrics['noise_desc'] = "Excellent (Very low noise)"
        elif noise_floor < 0.02:
            quality_metrics['noise_score'] = 8
            quality_metrics['noise_desc'] = "Good (Low noise)"
        elif noise_floor < 0.05:
            quality_metrics['noise_score'] = 6
            quality_metrics['noise_desc'] = "Fair (Moderate noise)"
        else:
            quality_metrics['noise_score'] = 4
            quality_metrics['noise_desc'] = "Poor (High noise)"
        
        # 4. Frequency content analysis
        stft = librosa.stft(y, n_fft=2048)
        magnitude = np.abs(stft)
        freq_content = np.mean(magnitude, axis=1)
        
        # Check for good high-frequency content (indicates quality recording)
        high_freq_content = np.mean(freq_content[len(freq_content)//2:])
        mid_freq_content = np.mean(freq_content[len(freq_content)//4:3*len(freq_content)//4])
        
        if high_freq_content / (mid_freq_content + 1e-10) > 0.3:
            quality_metrics['freq_score'] = 9
        elif high_freq_content / (mid_freq_content + 1e-10) > 0.1:
            quality_metrics['freq_score'] = 7
        else:
            quality_metrics['freq_score'] = 5
        
        # 5. Overall quality score
        overall_score = (
            quality_metrics['sample_rate_score'] * 0.3 +
            quality_metrics['dynamic_range_score'] * 0.2 +
            quality_metrics['noise_score'] * 0.3 +
            quality_metrics['freq_score'] * 0.2
        )
        
        quality_metrics['overall_score'] = overall_score
        self.audio_quality_score = overall_score
        
        # Determine processing mode
        if overall_score >= 8.5:
            self.processing_mode = "preserve"
            processing_desc = "PRESERVE MODE - Minimal processing to maintain quality"
        elif overall_score >= 6.5:
            self.processing_mode = "light"
            processing_desc = "LIGHT MODE - Conservative enhancement"
        else:
            self.processing_mode = "aggressive"
            processing_desc = "ENHANCEMENT MODE - Active noise reduction"
        
        print(f"📊 Audio Quality Analysis:")
        print(f"   Sample Rate: {sr} Hz ({quality_metrics['sample_rate_desc']})")
        print(f"   Duration: {duration:.2f} seconds ({duration/60:.1f} minutes)")
        print(f"   Noise Level: {quality_metrics['noise_desc']}")
        print(f"   Overall Score: {overall_score:.1f}/10")
        print(f"   🎯 Processing Mode: {processing_desc}")
        print()
        
        return quality_metrics

    def minimal_processing(self, audio: np.ndarray, sr: int) -> np.ndarray:
        """Apply only essential processing for high-quality audio."""
        # Just normalize to prevent clipping, preserve everything else
        if np.max(np.abs(audio)) > 0:
            audio = audio / np.max(np.abs(audio)) * 0.95
        return audio

    def light_processing(self, audio: np.ndarray, sr: int) -> np.ndarray:
        """Apply conservative processing for good quality audio."""
        # Very gentle high-pass filter only if needed
        nyquist = sr / 2
        low_cutoff = 60 / nyquist  # Remove only very low rumble
        if low_cutoff < 1.0:
            b, a = butter(2, low_cutoff, btype='high')  # Gentle 2nd order
            audio = filtfilt(b, a, audio)
        
        # Normalize
        if np.max(np.abs(audio)) > 0:
            audio = audio / np.max(np.abs(audio)) * 0.95
        return audio

    def enhanced_processing(self, audio: np.ndarray, sr: int) -> np.ndarray:
        """Apply more processing for lower quality audio."""
        # More aggressive filtering and noise reduction
        # (Similar to the original aggressive processing but more controlled)
        
        # High-pass filter
        nyquist = sr / 2
        low_cutoff = 80 / nyquist
        if low_cutoff < 1.0:
            b, a = butter(4, low_cutoff, btype='high')
            audio = filtfilt(b, a, audio)
        
        # Simple spectral subtraction for noise
        stft = librosa.stft(audio, n_fft=1024, hop_length=256)
        magnitude = np.abs(stft)
        phase = np.angle(stft)
        
        # Estimate noise from quietest 10%
        noise_estimate = np.percentile(magnitude, 10, axis=1, keepdims=True)
        
        # Gentle subtraction
        clean_magnitude = magnitude - 0.5 * noise_estimate
        clean_magnitude = np.maximum(clean_magnitude, 0.1 * magnitude)
        
        # Reconstruct
        clean_stft = clean_magnitude * np.exp(1j * phase)
        audio = librosa.istft(clean_stft, hop_length=256)
        
        # Normalize
        if np.max(np.abs(audio)) > 0:
            audio = audio / np.max(np.abs(audio)) * 0.95
        
        return audio

    def smart_audio_processing(self, audio: np.ndarray, sr: int) -> np.ndarray:
        """Apply processing based on detected quality level."""
        if self.processing_mode == "preserve":
            return self.minimal_processing(audio, sr)
        elif self.processing_mode == "light":
            return self.light_processing(audio, sr)
        else:
            return self.enhanced_processing(audio, sr)

    def segment_by_silence(self, audio_path: str) -> List[str]:
        """Split audio into segments with intelligent parameters."""
        print(f"✂️ Segmenting audio with quality-aware parameters...")
        
        audio = AudioSegment.from_wav(audio_path)
        
        # Adjust segmentation based on quality
        if self.processing_mode == "preserve":
            # More sensitive for high-quality audio
            min_silence = 600
            silence_thresh = -30
        else:
            # Less sensitive for lower quality
            min_silence = 800
            silence_thresh = -35
        
        segments = split_on_silence(
            audio,
            min_silence_len=min_silence,
            silence_thresh=silence_thresh,
            keep_silence=200  # Keep more context for high quality
        )
        
        print(f"📋 Found {len(segments)} audio segments")
        
        # Save segments with preserved quality
        segment_paths = []
        for i, segment in enumerate(segments):
            duration_seconds = len(segment) / 1000.0
            
            # Target 10-30 second range
            if 8.0 <= duration_seconds <= 35.0:
                segment_path = self.output_dir / "segments" / f"segment_{i:03d}.wav"
                # Export with original quality settings
                segment.export(segment_path, format="wav", 
                             parameters=["-ar", str(self.original_sample_rate)])
                segment_paths.append(str(segment_path))
        
        print(f"💾 Saved {len(segment_paths)} segments in target duration range")
        return segment_paths

    def create_test_samples(self, segment_paths: List[str], num_samples: int = 3) -> List[str]:
        """Create test samples for user to approve quality."""
        print(f"🎧 Creating {num_samples} test samples for quality verification...")
        
        if not segment_paths:
            return []
        
        # Select diverse samples (beginning, middle, end)
        test_indices = []
        if len(segment_paths) >= 3:
            test_indices = [0, len(segment_paths)//2, len(segment_paths)-1]
        else:
            test_indices = list(range(len(segment_paths)))
        
        test_samples = []
        
        for i, idx in enumerate(test_indices[:num_samples]):
            segment_path = segment_paths[idx]
            
            try:
                # Load with original sample rate
                y, sr = librosa.load(segment_path, sr=self.original_sample_rate)
                
                # Apply our smart processing
                y_processed = self.smart_audio_processing(y, sr)
                
                # Save test sample
                test_sample_path = self.output_dir / "test_samples" / f"test_sample_{i+1}_{Path(segment_path).stem}.wav"
                sf.write(test_sample_path, y_processed, sr)
                test_samples.append(str(test_sample_path))
                
                print(f"   ✅ Test sample {i+1}: {test_sample_path.name}")
                
            except Exception as e:
                print(f"   ⚠️ Could not create test sample {i+1}: {e}")
        
        return test_samples

    def get_user_approval(self, test_samples: List[str]) -> bool:
        """Get user approval for processing quality."""
        if not test_samples:
            return True
        
        print("\n" + "="*60)
        print("🎧 TEST SAMPLES READY FOR REVIEW")
        print("="*60)
        print("Please listen to the test samples in the test_samples/ folder:")
        print()
        
        for i, sample_path in enumerate(test_samples):
            print(f"   🎵 Test Sample {i+1}: {Path(sample_path).name}")
        
        print()
        print(f"Current processing mode: {self.processing_mode.upper()}")
        print("These samples show how your final clips will sound.")
        print()
        
        while True:
            response = input("👍 Are you happy with the quality? (y/n/s for settings): ").strip().lower()
            
            if response in ['y', 'yes']:
                print("🎯 Excellent! Proceeding with full processing...")
                return True
            elif response in ['n', 'no']:
                print("💡 No problem! Let's adjust the processing...")
                return self.adjust_processing_settings()
            elif response in ['s', 'settings']:
                return self.show_processing_options()
            else:
                print("Please enter 'y' for yes, 'n' for no, or 's' for settings.")

    def adjust_processing_settings(self) -> bool:
        """Allow user to adjust processing settings."""
        print("\n🔧 Processing Options:")
        print("1. preserve - Minimal processing (best for studio quality)")
        print("2. light - Conservative enhancement (good for clean recordings)")
        print("3. aggressive - Active noise reduction (for noisy audio)")
        print("4. quit - Stop processing")
        print()
        
        while True:
            choice = input("Select processing mode (1-4): ").strip()
            
            if choice == '1':
                self.processing_mode = "preserve"
                break
            elif choice == '2':
                self.processing_mode = "light"
                break
            elif choice == '3':
                self.processing_mode = "aggressive"
                break
            elif choice == '4':
                print("👋 Processing stopped. You can run again anytime!")
                return False
            else:
                print("Please enter 1, 2, 3, or 4.")
        
        print(f"\n🎯 Processing mode set to: {self.processing_mode.upper()}")
        print("Creating new test samples...")
        
        # Recreate test samples with new settings
        segment_paths = list(self.output_dir.glob("segments/*.wav"))
        test_samples = self.create_test_samples([str(p) for p in segment_paths])
        return self.get_user_approval(test_samples)

    def show_processing_options(self) -> bool:
        """Show detailed processing information and options."""
        print(f"\n📊 Current Audio Analysis:")
        print(f"   Quality Score: {self.audio_quality_score:.1f}/10")
        print(f"   Sample Rate: {self.original_sample_rate} Hz")
        print(f"   Processing Mode: {self.processing_mode.upper()}")
        print()
        
        if self.processing_mode == "preserve":
            print("🔒 PRESERVE MODE:")
            print("   - Only normalizes volume")
            print("   - Maintains original frequency content")
            print("   - Best for studio/podcast quality audio")
        elif self.processing_mode == "light":
            print("🪶 LIGHT MODE:")
            print("   - Gentle high-pass filter (removes rumble)")
            print("   - Volume normalization")
            print("   - Preserves most original character")
        else:
            print("🔧 AGGRESSIVE MODE:")
            print("   - High-pass filtering")
            print("   - Spectral noise reduction")
            print("   - Best for noisy recordings")
        
        return self.adjust_processing_settings()

    def extract_voice_features(self, audio_path: str) -> Dict:
        """Extract voice characteristics with quality-preserving processing."""
        try:
            # Load with original sample rate
            y, sr = librosa.load(audio_path, sr=self.original_sample_rate)
            
            if len(y) < sr * 0.5:
                return None
            
            # Apply minimal processing for feature extraction
            y_clean = self.smart_audio_processing(y, sr)
            
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
            
            # MFCC features
            mfccs = librosa.feature.mfcc(y=y_clean, sr=sr, n_mfcc=13)
            features['mfcc_mean'] = np.mean(mfccs, axis=1)
            features['mfcc_std'] = np.std(mfccs, axis=1)
            
            # Voice quality indicators
            features['zero_crossing_rate'] = np.mean(librosa.feature.zero_crossing_rate(y_clean)[0])
            features['rms_energy'] = np.mean(librosa.feature.rms(y=y_clean)[0])
            features['duration'] = len(y_clean) / sr
            
            # Harmonic-to-noise ratio
            harmonic, percussive = librosa.effects.hpss(y_clean)
            harmonic_energy = np.mean(harmonic**2)
            percussive_energy = np.mean(percussive**2)
            features['harmonic_ratio'] = harmonic_energy / (harmonic_energy + percussive_energy) if percussive_energy > 0 else 1.0
            
            return features
            
        except Exception as e:
            print(f"⚠️ Could not analyze {Path(audio_path).name}: {e}")
            return None

    def analyze_all_segments(self, segment_paths: List[str]) -> List[Dict]:
        """Analyze all segments with quality-preserving approach."""
        print(f"🔍 Analyzing voice characteristics (quality-preserving mode)...")
        print()
        
        if not segment_paths:
            print("❌ No segments found to analyze!")
            return []
        
        print(f"📊 Found {len(segment_paths)} segments to analyze")
        
        analyzed_segments = []
        
        for i, segment_path in enumerate(segment_paths):
            if i % 10 == 0:
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
        """Group similar voices together."""
        print(f"🤖 Grouping similar voices together...")
        print()
        
        if len(analyzed_segments) < 6:
            print("⚠️ Not enough segments for automatic clustering")
            return self.manual_classification_helper(analyzed_segments)
        
        # Same clustering logic as before but with preserved quality
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
            
            vector.extend(features['mfcc_mean'][:5])
            
            if not any(np.isnan(vector)) and not any(np.isinf(vector)):
                feature_vectors.append(vector)
                valid_segments.append(segment)
        
        if len(feature_vectors) < 6:
            print("⚠️ Not enough valid features for clustering")
            return self.manual_classification_helper(analyzed_segments)
        
        # Normalize and cluster
        feature_matrix = np.array(feature_vectors)
        feature_matrix = (feature_matrix - np.mean(feature_matrix, axis=0)) / (np.std(feature_matrix, axis=0) + 1e-8)
        
        n_clusters = min(n_clusters, max(2, len(feature_vectors) // 3))
        kmeans = KMeans(n_clusters=n_clusters, random_state=42, n_init=10)
        cluster_labels = kmeans.fit_predict(feature_matrix)
        
        clusters = {}
        for i, segment in enumerate(valid_segments):
            cluster_id = cluster_labels[i]
            if cluster_id not in clusters:
                clusters[cluster_id] = []
            clusters[cluster_id].append(segment)
        
        print(f"🎯 Found {len(clusters)} distinct voice groups:")
        for cluster_id, segments in clusters.items():
            avg_pitch = np.mean([s['features']['mean_pitch'] for s in segments])
            avg_duration = np.mean([s['features']['duration'] for s in segments])
            print(f"   Group {cluster_id}: {len(segments)} segments, avg pitch: {avg_pitch:.1f} Hz, avg duration: {avg_duration:.1f}s")
        
        return clusters

    def identify_target_cluster(self, clusters: Dict) -> Tuple[int, List[str]]:
        """Identify the target chess master's voice cluster."""
        print(f"\n🕵️ Identifying {self.master_display_name}'s voice characteristics...")
        
        cluster_scores = {}
        
        for cluster_id, segments in clusters.items():
            pitches = [s['features']['mean_pitch'] for s in segments]
            durations = [s['features']['duration'] for s in segments]
            harmonic_ratios = [s['features']['harmonic_ratio'] for s in segments]
            
            avg_pitch = np.mean(pitches)
            avg_duration = np.mean(durations)
            avg_clarity = np.mean(harmonic_ratios)
            
            score = 0
            reasoning = []
            
            # Pitch scoring
            if 90 <= avg_pitch <= 200:
                score += 3
                reasoning.append("good pitch range for chess master")
            elif 70 <= avg_pitch <= 250:
                score += 1
                reasoning.append("possible pitch range")
            
            # Clarity scoring (adjusted for processing mode)
            clarity_threshold = 0.8 if self.processing_mode == "preserve" else 0.6
            if avg_clarity > clarity_threshold:
                score += 3
                reasoning.append("excellent clarity")
            elif avg_clarity > clarity_threshold - 0.2:
                score += 2
                reasoning.append("good clarity")
            
            # Duration scoring
            if 10 <= avg_duration <= 30:
                score += 3
                reasoning.append("ideal duration for training")
            elif 8 <= avg_duration <= 35:
                score += 2
                reasoning.append("good duration for training")
            
            # Segment count preference
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

    def create_final_clips(self, segments: List[Dict], output_folder: str):
        """Create final clips with quality-preserving processing."""
        print(f"🎵 Creating final clips with {self.processing_mode} processing...")
        
        output_path = self.output_dir / output_folder
        processed_count = 0
        
        for segment in segments:
            try:
                # Load with original sample rate
                y, sr = librosa.load(segment['filepath'], sr=self.original_sample_rate)
                
                # Apply our intelligent processing
                y_processed = self.smart_audio_processing(y, sr)
                
                # Save with original quality
                processed_filename = f"processed_{Path(segment['filepath']).name}"
                processed_path = output_path / processed_filename
                sf.write(processed_path, y_processed, sr)
                processed_count += 1
                
            except Exception as e:
                print(f"⚠️ Could not process {segment['filename']}: {e}")
        
        print(f"   ✅ Created {processed_count} processed clips")

    def organize_segments(self, clusters: Dict, target_cluster_id: int):
        """Organize segments into folders with quality preservation."""
        print("📁 Organizing segments with quality preservation...")
        
        target_segments = clusters.get(target_cluster_id, [])
        other_segments = []
        for cluster_id, segments in clusters.items():
            if cluster_id != target_cluster_id:
                other_segments.extend(segments)
        
        # Process target segments
        print(f"🎯 Processing {len(target_segments)} likely {self.master_display_name} segments...")
        self.create_final_clips(target_segments, "likely_target")
        
        # Copy other segments without processing
        print(f"👥 Copying {len(other_segments)} other speaker segments...")
        for segment in other_segments:
            src = Path(segment['filepath'])
            dst = self.output_dir / "likely_others" / src.name
            shutil.copy2(src, dst)
        
        # Create premium training-ready clips
        training_segments = [s for s in target_segments 
                           if 10.0 <= s['features']['duration'] <= 30.0]
        
        if len(training_segments) > 15:
            training_segments.sort(key=lambda s: s['features']['harmonic_ratio'], reverse=True)
            training_segments = training_segments[:15]
        
        print(f"🎵 Creating {len(training_segments)} premium training-ready clips...")
        for i, segment in enumerate(training_segments):
            try:
                y, sr = librosa.load(segment['filepath'], sr=self.original_sample_rate)
                y_processed = self.smart_audio_processing(y, sr)
                
                clean_filename = f"{self.master_name.lower().replace(' ', '_')}_{i:03d}.wav"
                clean_path = self.output_dir / "training_ready" / clean_filename
                sf.write(clean_path, y_processed, sr)
                
            except Exception as e:
                print(f"⚠️ Could not process training clip {i}: {e}")
        
        # Manual review samples
        manual_review_segments = target_segments[:8]
        print(f"📋 Creating {len(manual_review_segments)} clips for manual review...")
        self.create_final_clips(manual_review_segments, "manual_review")

    def manual_classification_helper(self, analyzed_segments: List[Dict]) -> Dict:
        """Helper for manual classification when clustering fails."""
        print("💙 Creating manual classification helper...")
        
        analyzed_segments.sort(key=lambda x: x['features']['mean_pitch'])
        
        low_pitch = [s for s in analyzed_segments if s['features']['mean_pitch'] < 120]
        medium_pitch = [s for s in analyzed_segments if 120 <= s['features']['mean_pitch'] < 160]
        high_pitch = [s for s in analyzed_segments if s['features']['mean_pitch'] >= 160]
        
        clusters = {}
        if low_pitch: clusters[0] = low_pitch
        if medium_pitch: clusters[1] = medium_pitch
        if high_pitch: clusters[2] = high_pitch
        
        return clusters

    def create_summary_report(self, clusters: Dict, target_cluster_id: int, youtube_url: str):
        """Create comprehensive summary report."""
        report_path = self.output_dir / "extraction_report.txt"
        
        target_segments = clusters.get(target_cluster_id, [])
        total_segments = sum(len(segments) for segments in clusters.values())
        training_ready = len(list((self.output_dir / "training_ready").glob("*.wav")))
        
        with open(report_path, 'w') as f:
            f.write(f"{self.master_display_name} Intelligent Voice Extraction Report\n")
            f.write("=" * 60 + "\n\n")
            f.write(f"Source URL: {youtube_url}\n")
            f.write(f"Target Speaker: {self.master_display_name}\n")
            f.write(f"Processing Mode: {self.processing_mode.upper()}\n")
            f.write(f"Original Sample Rate: {self.original_sample_rate} Hz\n")
            f.write(f"Audio Quality Score: {self.audio_quality_score:.1f}/10\n\n")
            
            f.write("RESULTS SUMMARY:\n")
            f.write(f"Total segments analyzed: {total_segments}\n")
            f.write(f"Likely {self.master_display_name} segments: {len(target_segments)}\n")
            f.write(f"Training-ready clips (10-30s): {training_ready}\n\n")
            
            f.write("PROCESSING APPLIED:\n")
            if self.processing_mode == "preserve":
                f.write("- PRESERVE MODE: Minimal processing, maximum quality retention\n")
                f.write("- Only volume normalization applied\n")
            elif self.processing_mode == "light":
                f.write("- LIGHT MODE: Conservative enhancement\n")
                f.write("- Gentle high-pass filtering for rumble removal\n")
            else:
                f.write("- ENHANCEMENT MODE: Active noise reduction\n")
                f.write("- High-pass filtering and spectral noise reduction\n")
            
            f.write(f"- Original sample rate preserved: {self.original_sample_rate} Hz\n\n")
            
            f.write("FOLDER ORGANIZATION:\n")
            f.write("- training_ready/: Premium clips optimized for TTS training\n")
            f.write("- manual_review/: Sample clips for verification\n")
            f.write("- likely_target/: All identified target speaker clips\n")
            f.write("- test_samples/: Quality test samples created during processing\n\n")
            
            f.write("NEXT STEPS:\n")
            f.write("1. Review clips in training_ready/ folder\n")
            f.write("2. Upload 10-20 best clips to ElevenLabs\n")
            f.write("3. Ensure variety in content and emotion\n")
            f.write("4. Train your voice model!\n")
        
        print(f"📋 Created detailed report: {report_path}")

    def process_youtube_video(self, url: str) -> str:
        """Complete intelligent processing pipeline."""
        print(f"🚀 Starting {self.master_display_name} intelligent voice extraction...")
        print(f"🔗 Source URL: {url}")
        print("=" * 60)
        
        try:
            # Step 1: Download audio
            audio_path = self.download_youtube_audio(url)
            
            # Step 2: Analyze quality and determine processing mode
            quality_analysis = self.analyze_audio_quality(audio_path)
            
            # Step 3: Segment audio
            segments = self.segment_by_silence(audio_path)
            
            if not segments:
                print("❌ No suitable segments found!")
                return None
            
            # Step 4: Create test samples for user approval
            test_samples = self.create_test_samples(segments)
            
            # Step 5: Get user approval
            if not self.get_user_approval(test_samples):
                print("👋 Processing stopped by user.")
                return None
            
            # Step 6: Analyze voice characteristics
            analyzed_segments = self.analyze_all_segments(segments)
            
            if not analyzed_segments:
                print("❌ Could not analyze voice characteristics!")
                return None
            
            # Step 7: Cluster voices
            clusters = self.cluster_voices(analyzed_segments)
            
            if not clusters:
                print("❌ Could not group voices!")
                return None
            
            # Step 8: Identify target speaker
            target_cluster_id, reasoning = self.identify_target_cluster(clusters)
            
            # Step 9: Organize segments with quality preservation
            self.organize_segments(clusters, target_cluster_id)
            
            # Step 10: Create report
            self.create_summary_report(clusters, target_cluster_id, url)
            
            # Summary
            target_count = len(clusters.get(target_cluster_id, []))
            training_ready = len(list((self.output_dir / "training_ready").glob("*.wav")))
            
            print("=" * 60)
            print("🎉 Intelligent voice extraction completed!")
            print(f"🎯 Found {target_count} likely {self.master_display_name} segments")
            print(f"🎵 Created {training_ready} training-ready clips")
            print(f"🧠 Processing mode: {self.processing_mode.upper()}")
            print(f"📈 Quality preserved at {self.original_sample_rate} Hz")
            print(f"📁 Results location: {self.output_dir}")
            print("=" * 60)
            
            return str(self.output_dir)
            
        except Exception as e:
            print(f"❌ Error in processing pipeline: {e}")
            print("💙 Don't worry - this is part of the learning process!")
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


def main():
    """Main execution with intelligent quality detection."""
    print("🧠 Intelligent Chess Master Voice Extractor")
    print("===========================================")
    print("Smart quality detection • Interactive approval • Quality preservation")
    print()
    
    # Get chess master name
    print("Which chess master would you like to extract voices for?")
    master_name = input("🏆 Chess Master Name: ").strip()
    
    if not master_name:
        print("❌ Please provide a chess master name!")
        return
    
    print(f"\n🎯 Excellent! Let's extract {master_name.title()}'s voice intelligently.")
    
    # Get YouTube URL
    print("\nPlease provide a YouTube URL:")
    youtube_url = input("🔗 YouTube URL: ").strip()
    
    if not youtube_url or 'youtube.com' not in youtube_url and 'youtu.be' not in youtube_url:
        print("❌ Please provide a valid YouTube URL!")
        return
    
    print(f"\n🚀 Starting intelligent processing for {master_name.title()}...")
    print("The system will analyze quality and create test samples for your approval.")
    print()
    
    # Install dependencies
    install_deps = input("📦 Install/check required packages? (y/n): ").strip().lower()
    if install_deps in ['y', 'yes', '']:
        install_requirements()
        print()
    
    try:
        extractor = IntelligentVoiceExtractor(master_name)
        result_path = extractor.process_youtube_video(youtube_url)
        
        if result_path:
            print("\n🎯 SUCCESS! Your voice extraction is complete!")
            print("=" * 50)
            print("📂 Check the training_ready/ folder for your clips")
            print("🎧 All clips preserve maximum quality for your settings")
            print("📤 Upload the best 10-20 clips to ElevenLabs")
            print(f"📁 Results: {result_path}")
            print("💙 Happy voice cloning!")
        
    except KeyboardInterrupt:
        print("\n💙 Processing interrupted. You can resume anytime!")
    except Exception as e:
        print(f"\n💙 Challenge encountered: {e}")
        print("Feel free to try with different settings or videos!")


if __name__ == "__main__":
    main()