import os
import sys
import json
import shutil
from pathlib import Path
import numpy as np
import librosa
import soundfile as sf
from typing import List, Dict, Tuple
import matplotlib.pyplot as plt
from scipy.spatial.distance import cosine
from sklearn.cluster import KMeans
import warnings
warnings.filterwarnings('ignore')

class FischerVoiceIdentifier:
    """
    A gentle tool to help identify and isolate Bobby Fischer's voice
    from a collection of audio clips containing multiple speakers.
    """
    
    def __init__(self, clips_directory: str):
        self.clips_dir = Path(clips_directory)
        self.output_dir = self.clips_dir.parent / "identified_fischer_voices"
        self.output_dir.mkdir(exist_ok=True)
        
        # Create organized folders
        (self.output_dir / "likely_fischer").mkdir(exist_ok=True)
        (self.output_dir / "likely_others").mkdir(exist_ok=True)
        (self.output_dir / "uncertain").mkdir(exist_ok=True)
        (self.output_dir / "manual_review").mkdir(exist_ok=True)
        
        print("🎯 Bobby Fischer Voice Identifier")
        print("=================================")
        print(f"📁 Analyzing clips from: {self.clips_dir}")
        print(f"📁 Results will be saved to: {self.output_dir}")
        print()
    
    def extract_voice_features(self, audio_path: str) -> Dict:
        """Extract voice characteristics that help identify speakers."""
        try:
            # Load audio
            y, sr = librosa.load(audio_path, sr=22050)
            
            # Skip very short clips
            if len(y) < sr * 0.5:  # Less than 0.5 seconds
                return None
            
            # Extract features that distinguish speakers
            features = {}
            
            # 1. Fundamental frequency (pitch) characteristics
            f0, voiced_flag, voiced_probs = librosa.pyin(y, fmin=50, fmax=300, sr=sr)
            f0_clean = f0[voiced_flag]
            if len(f0_clean) > 0:
                features['mean_pitch'] = np.nanmean(f0_clean)
                features['pitch_std'] = np.nanstd(f0_clean)
                features['pitch_range'] = np.nanmax(f0_clean) - np.nanmin(f0_clean)
            else:
                features['mean_pitch'] = 0
                features['pitch_std'] = 0
                features['pitch_range'] = 0
            
            # 2. Spectral characteristics
            spectral_centroids = librosa.feature.spectral_centroid(y=y, sr=sr)[0]
            features['spectral_centroid_mean'] = np.mean(spectral_centroids)
            features['spectral_centroid_std'] = np.std(spectral_centroids)
            
            # 3. MFCC features (voice "fingerprint")
            mfccs = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
            features['mfcc_mean'] = np.mean(mfccs, axis=1)
            features['mfcc_std'] = np.std(mfccs, axis=1)
            
            # 4. Voice quality indicators
            features['zero_crossing_rate'] = np.mean(librosa.feature.zero_crossing_rate(y)[0])
            features['rms_energy'] = np.mean(librosa.feature.rms(y=y)[0])
            
            # 5. Duration and speech rate
            features['duration'] = len(y) / sr
            
            # 6. Harmonic-to-noise ratio (voice clarity)
            harmonic, percussive = librosa.effects.hpss(y)
            harmonic_energy = np.mean(harmonic**2)
            percussive_energy = np.mean(percussive**2)
            if percussive_energy > 0:
                features['harmonic_ratio'] = harmonic_energy / (harmonic_energy + percussive_energy)
            else:
                features['harmonic_ratio'] = 1.0
            
            return features
            
        except Exception as e:
            print(f"⚠️ Could not analyze {Path(audio_path).name}: {e}")
            return None
    
    def analyze_all_clips(self) -> List[Dict]:
        """Analyze all audio clips and extract their voice characteristics."""
        print("🔍 Analyzing voice characteristics of all clips...")
        print("This might take a few minutes - we're being thorough! ☕")
        print()
        
        audio_files = list(self.clips_dir.glob("*.wav"))
        if not audio_files:
            print("❌ No WAV files found in the clips directory!")
            return []
        
        print(f"📊 Found {len(audio_files)} audio files to analyze")
        
        analyzed_clips = []
        
        for i, audio_file in enumerate(audio_files):
            if i % 50 == 0:  # Progress update every 50 files
                print(f"🔄 Analyzed {i}/{len(audio_files)} clips...")
            
            features = self.extract_voice_features(str(audio_file))
            if features:
                clip_info = {
                    'filename': audio_file.name,
                    'filepath': str(audio_file),
                    'features': features
                }
                analyzed_clips.append(clip_info)
        
        print(f"✅ Successfully analyzed {len(analyzed_clips)} clips!")
        print()
        
        return analyzed_clips
    
    def cluster_voices(self, analyzed_clips: List[Dict], n_clusters: int = 3) -> Dict:
        """Group similar voices together using machine learning."""
        print(f"🤖 Grouping similar voices together...")
        print("We're looking for patterns that distinguish different speakers.")
        print()
        
        if len(analyzed_clips) < 10:
            print("⚠️ Not enough clips for automatic clustering")
            return self.manual_classification_helper(analyzed_clips)
        
        # Prepare feature vectors for clustering
        feature_vectors = []
        valid_clips = []
        
        for clip in analyzed_clips:
            features = clip['features']
            
            # Create a feature vector combining key characteristics
            vector = [
                features['mean_pitch'],
                features['pitch_std'],
                features['spectral_centroid_mean'],
                features['zero_crossing_rate'],
                features['harmonic_ratio']
            ]
            
            # Add MFCC means (first few coefficients are most important)
            vector.extend(features['mfcc_mean'][:5])
            
            # Check for valid features
            if not any(np.isnan(vector)) and not any(np.isinf(vector)):
                feature_vectors.append(vector)
                valid_clips.append(clip)
        
        if len(feature_vectors) < 10:
            print("⚠️ Not enough valid features for clustering")
            return self.manual_classification_helper(analyzed_clips)
        
        # Normalize features
        feature_matrix = np.array(feature_vectors)
        feature_matrix = (feature_matrix - np.mean(feature_matrix, axis=0)) / np.std(feature_matrix, axis=0)
        
        # Perform clustering
        kmeans = KMeans(n_clusters=min(n_clusters, len(feature_vectors)//3), random_state=42)
        cluster_labels = kmeans.fit_predict(feature_matrix)
        
        # Group clips by cluster
        clusters = {}
        for i, clip in enumerate(valid_clips):
            cluster_id = cluster_labels[i]
            if cluster_id not in clusters:
                clusters[cluster_id] = []
            clusters[cluster_id].append(clip)
        
        # Analyze clusters
        print(f"🎯 Found {len(clusters)} distinct voice groups:")
        for cluster_id, clips in clusters.items():
            avg_pitch = np.mean([c['features']['mean_pitch'] for c in clips])
            print(f"   Group {cluster_id}: {len(clips)} clips, avg pitch: {avg_pitch:.1f} Hz")
        
        return clusters
    
    def identify_fischer_cluster(self, clusters: Dict) -> Tuple[int, str]:
        """Try to identify which cluster is most likely Bobby Fischer."""
        print("\n🕵️ Trying to identify Bobby Fischer's voice characteristics...")
        
        # Fischer's voice characteristics (from historical knowledge):
        # - American accent
        # - Medium to higher pitch for a male
        # - Clear articulation
        # - Confident speech patterns
        # - Born 1943, so in interview he'd be middle-aged (deeper than young adult)
        
        cluster_scores = {}
        
        for cluster_id, clips in clusters.items():
            # Calculate average characteristics
            pitches = [c['features']['mean_pitch'] for c in clips]
            durations = [c['features']['duration'] for c in clips]
            harmonic_ratios = [c['features']['harmonic_ratio'] for c in clips]
            
            avg_pitch = np.mean(pitches)
            avg_duration = np.mean(durations)
            avg_clarity = np.mean(harmonic_ratios)
            
            # Scoring based on expected Fischer characteristics
            score = 0
            reasoning = []
            
            # Pitch scoring (Fischer had a medium-higher male voice)
            if 100 <= avg_pitch <= 180:
                score += 3
                reasoning.append("good pitch range for Fischer")
            elif 80 <= avg_pitch <= 220:
                score += 1
                reasoning.append("possible pitch range")
            
            # Clarity scoring (Fischer spoke clearly)
            if avg_clarity > 0.7:
                score += 2
                reasoning.append("clear speech")
            elif avg_clarity > 0.5:
                score += 1
                reasoning.append("reasonably clear")
            
            # Duration scoring (Fischer often gave thoughtful, longer responses)
            if avg_duration > 3:
                score += 2
                reasoning.append("substantial responses")
            elif avg_duration > 1.5:
                score += 1
                reasoning.append("decent length responses")
            
            cluster_scores[cluster_id] = {
                'score': score,
                'reasoning': reasoning,
                'stats': {
                    'avg_pitch': avg_pitch,
                    'avg_duration': avg_duration,
                    'avg_clarity': avg_clarity,
                    'clip_count': len(clips)
                }
            }
        
        # Find the most likely Fischer cluster
        best_cluster = max(cluster_scores.keys(), key=lambda k: cluster_scores[k]['score'])
        
        print("🎯 Voice analysis results:")
        for cluster_id in sorted(cluster_scores.keys()):
            info = cluster_scores[cluster_id]
            marker = "🎯 LIKELY FISCHER" if cluster_id == best_cluster else "   Other speaker"
            print(f"{marker} - Group {cluster_id}:")
            print(f"    {info['stats']['clip_count']} clips, pitch: {info['stats']['avg_pitch']:.1f}Hz")
            print(f"    Duration: {info['stats']['avg_duration']:.1f}s, clarity: {info['stats']['avg_clarity']:.2f}")
            print(f"    Reasoning: {', '.join(info['reasoning'])}")
            print()
        
        return best_cluster, cluster_scores[best_cluster]['reasoning']
    
    def organize_clips(self, clusters: Dict, fischer_cluster_id: int):
        """Organize clips into folders based on identification."""
        print("📁 Organizing clips into folders...")
        
        fischer_clips = clusters.get(fischer_cluster_id, [])
        other_clips = []
        for cluster_id, clips in clusters.items():
            if cluster_id != fischer_cluster_id:
                other_clips.extend(clips)
        
        # Copy Fischer clips
        print(f"🎯 Copying {len(fischer_clips)} likely Fischer clips...")
        for clip in fischer_clips:
            src = Path(clip['filepath'])
            dst = self.output_dir / "likely_fischer" / src.name
            shutil.copy2(src, dst)
        
        # Copy other clips
        print(f"👥 Copying {len(other_clips)} other speaker clips...")
        for clip in other_clips:
            src = Path(clip['filepath'])
            dst = self.output_dir / "likely_others" / src.name
            shutil.copy2(src, dst)
        
        # Create a sample for manual review
        manual_review_clips = fischer_clips[:20]  # First 20 Fischer clips
        print(f"📋 Creating {len(manual_review_clips)} clips for manual review...")
        for clip in manual_review_clips:
            src = Path(clip['filepath'])
            dst = self.output_dir / "manual_review" / src.name
            shutil.copy2(src, dst)
    
    def create_summary_report(self, clusters: Dict, fischer_cluster_id: int):
        """Create a helpful summary report."""
        report_path = self.output_dir / "identification_report.txt"
        
        fischer_clips = clusters.get(fischer_cluster_id, [])
        total_clips = sum(len(clips) for clips in clusters.values())
        other_clips = total_clips - len(fischer_clips)
        
        with open(report_path, 'w') as f:
            f.write("Bobby Fischer Voice Identification Report\n")
            f.write("========================================\n\n")
            f.write(f"Total clips analyzed: {total_clips}\n")
            f.write(f"Likely Fischer clips: {len(fischer_clips)}\n")
            f.write(f"Other speaker clips: {other_clips}\n\n")
            f.write("Folder Organization:\n")
            f.write("- likely_fischer/: Clips identified as Bobby Fischer\n")
            f.write("- likely_others/: Clips from other speakers\n")
            f.write("- manual_review/: Sample of Fischer clips to verify\n\n")
            f.write("Next Steps:\n")
            f.write("1. Listen to clips in manual_review/ folder\n")
            f.write("2. Verify they sound like Bobby Fischer\n")
            f.write("3. If good, use likely_fischer/ folder for training\n")
            f.write("4. If not, adjust and re-run analysis\n\n")
            f.write("For ElevenLabs training:\n")
            f.write("- Use 10-20 of the clearest Fischer clips\n")
            f.write("- Each clip should be 2-10 seconds\n")
            f.write("- Ensure good audio quality\n")
        
        print(f"📋 Created detailed report: {report_path}")
    
    def manual_classification_helper(self, analyzed_clips: List[Dict]) -> Dict:
        """Helper for when automatic clustering isn't possible."""
        print("💙 Creating a helper for manual classification...")
        
        # Sort by pitch to help group similar voices
        analyzed_clips.sort(key=lambda x: x['features']['mean_pitch'])
        
        # Create three groups based on pitch ranges
        low_pitch = [c for c in analyzed_clips if c['features']['mean_pitch'] < 120]
        medium_pitch = [c for c in analyzed_clips if 120 <= c['features']['mean_pitch'] < 160]
        high_pitch = [c for c in analyzed_clips if c['features']['mean_pitch'] >= 160]
        
        clusters = {}
        if low_pitch:
            clusters[0] = low_pitch
        if medium_pitch:
            clusters[1] = medium_pitch
        if high_pitch:
            clusters[2] = high_pitch
        
        return clusters
    
    def identify_fischer_voice(self):
        """Main process to identify Fischer's voice from all clips."""
        print("🚀 Starting Bobby Fischer voice identification...")
        print("This is such a fascinating challenge - let's solve it together!")
        print()
        
        try:
            # Step 1: Analyze all clips
            analyzed_clips = self.analyze_all_clips()
            if not analyzed_clips:
                print("❌ No clips could be analyzed. Check your audio files!")
                return
            
            # Step 2: Group similar voices
            clusters = self.cluster_voices(analyzed_clips)
            if not clusters:
                print("❌ Could not group voices. Try manual review instead!")
                return
            
            # Step 3: Identify Fischer cluster
            fischer_cluster_id, reasoning = self.identify_fischer_cluster(clusters)
            
            # Step 4: Organize clips
            self.organize_clips(clusters, fischer_cluster_id)
            
            # Step 5: Create report
            self.create_summary_report(clusters, fischer_cluster_id)
            
            print("🎉 Voice identification complete!")
            print("=================================")
            print(f"📁 Results are in: {self.output_dir}")
            print("💙 Next steps:")
            print("1. Listen to a few clips in the 'manual_review' folder")
            print("2. If they sound like Fischer, you're all set!")
            print("3. Use the 'likely_fischer' folder for your ElevenLabs training")
            print()
            print("You're doing amazing work on ChessPedagogue! 🎯♟️")
            
        except Exception as e:
            print(f"💙 We encountered a small challenge: {e}")
            print("This is completely normal - voice analysis is complex!")
            print("💡 You might want to try manual review of your clips instead.")


def main():
    print("🎭 Bobby Fischer Voice Identifier")
    print("=================================")
    print("Let's help identify Bobby Fischer's voice from your collection!")
    print()
    
    # Ask for the clips directory
    clips_dir = input("📁 Enter the path to your extracted clips folder: ").strip()
    if not clips_dir:
        # Default to common locations
        possible_dirs = [
            "bobby_fischer_voice_extraction/training_dataset",
            "extracted_voices/final_clips",
            "."
        ]
        
        for dir_path in possible_dirs:
            if Path(dir_path).exists() and list(Path(dir_path).glob("*.wav")):
                clips_dir = dir_path
                print(f"🎯 Found clips in: {clips_dir}")
                break
    
    if not clips_dir or not Path(clips_dir).exists():
        print("❌ Could not find the clips directory!")
        print("💡 Make sure to provide the path to your extracted audio clips")
        return
    
    try:
        identifier = FischerVoiceIdentifier(clips_dir)
        identifier.identify_fischer_voice()
        
    except KeyboardInterrupt:
        print("\n💙 No worries! You can run this again anytime.")
    except Exception as e:
        print(f"\n💙 We hit a small bump: {e}")
        print("You're doing wonderfully - this kind of challenge is part of learning!")

if __name__ == "__main__":
    main()