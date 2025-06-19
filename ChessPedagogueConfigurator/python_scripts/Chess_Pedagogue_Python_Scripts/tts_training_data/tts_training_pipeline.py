import os
import sys
import subprocess
import json
from pathlib import Path
import librosa
import soundfile as sf
import numpy as np
from pydub import AudioSegment
from pydub.silence import split_on_silence
import matplotlib.pyplot as plt
from typing import List, Tuple, Dict
import warnings
warnings.filterwarnings('ignore')

class VoiceExtractor:
    """
    A comprehensive tool for extracting specific speaker voices from YouTube videos.
    Perfect for creating voice models for TTS applications.
    """
    
    def __init__(self, output_dir: str = "extracted_voices"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(exist_ok=True)
        
        # Create subdirectories
        (self.output_dir / "raw_audio").mkdir(exist_ok=True)
        (self.output_dir / "segments").mkdir(exist_ok=True)
        (self.output_dir / "final_clips").mkdir(exist_ok=True)
        
        print("🎯 Voice Extraction Tool Initialized")
        print(f"📁 Output directory: {self.output_dir.absolute()}")
    
    def download_youtube_audio(self, url: str, filename: str = "fischer_interview") -> str:
        """Download high-quality audio from YouTube video."""
        print(f"📥 Downloading audio from YouTube...")
        
        output_path = self.output_dir / "raw_audio" / f"{filename}.wav"
        
        # Use yt-dlp to download best audio quality
        cmd = [
            "yt-dlp",
            "--extract-audio",
            "--audio-format", "wav",
            "--audio-quality", "0",  # Best quality
            "--output", str(output_path).replace('.wav', '.%(ext)s'),
            url
        ]
        
        try:
            subprocess.run(cmd, check=True, capture_output=True)
            print(f"✅ Audio downloaded successfully!")
            return str(output_path)
        except subprocess.CalledProcessError as e:
            print(f"❌ Error downloading: {e}")
            # Fallback: try with youtube-dl
            print("🔄 Trying alternative downloader...")
            cmd[0] = "youtube-dl"
            subprocess.run(cmd, check=True)
            return str(output_path)
    
    def analyze_audio_properties(self, audio_path: str) -> Dict:
        """Analyze the audio file to understand its properties."""
        print(f"🔍 Analyzing audio properties...")
        
        # Load audio
        y, sr = librosa.load(audio_path, sr=None)
        duration = len(y) / sr
        
        # Basic analysis
        properties = {
            'duration': duration,
            'sample_rate': sr,
            'channels': 1 if y.ndim == 1 else y.shape[0],
            'total_samples': len(y)
        }
        
        print(f"📊 Audio Properties:")
        print(f"   Duration: {duration:.2f} seconds ({duration/60:.1f} minutes)")
        print(f"   Sample Rate: {sr} Hz")
        print(f"   Total Samples: {len(y):,}")
        
        return properties
    
    def segment_by_silence(self, audio_path: str, min_silence_len: int = 500, 
                          silence_thresh: int = -40) -> List[str]:
        """Split audio into segments based on silence detection."""
        print(f"✂️ Segmenting audio by silence...")
        
        # Load audio with pydub
        audio = AudioSegment.from_wav(audio_path)
        
        # Split on silence
        segments = split_on_silence(
            audio,
            min_silence_len=min_silence_len,  # milliseconds
            silence_thresh=silence_thresh,    # dB
            keep_silence=100  # Keep some silence at edges
        )
        
        print(f"📋 Found {len(segments)} audio segments")
        
        # Save segments
        segment_paths = []
        for i, segment in enumerate(segments):
            if len(segment) > 1000:  # Only keep segments longer than 1 second
                segment_path = self.output_dir / "segments" / f"segment_{i:03d}.wav"
                segment.export(segment_path, format="wav")
                segment_paths.append(str(segment_path))
        
        print(f"💾 Saved {len(segment_paths)} valid segments")
        return segment_paths
    
    def speaker_diarization_simple(self, audio_path: str) -> List[Tuple[float, float, str]]:
        """
        Simple speaker diarization using spectral clustering.
        For more advanced diarization, consider using pyannote.audio
        """
        print(f"👥 Performing speaker analysis...")
        
        y, sr = librosa.load(audio_path, sr=22050)
        
        # Extract MFCC features for speaker identification
        mfccs = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
        
        # Simple voice activity detection
        frame_length = 2048
        hop_length = 512
        
        # RMS energy for voice activity detection
        rms = librosa.feature.rms(y=y, frame_length=frame_length, hop_length=hop_length)[0]
        
        # Threshold for voice activity (adjust as needed)
        voice_threshold = np.percentile(rms, 30)
        voice_frames = rms > voice_threshold
        
        # Convert frame indices to time
        times = librosa.frames_to_time(np.arange(len(voice_frames)), sr=sr, hop_length=hop_length)
        
        # Group consecutive voice frames
        voice_segments = []
        start_time = None
        
        for i, (time, is_voice) in enumerate(zip(times, voice_frames)):
            if is_voice and start_time is None:
                start_time = time
            elif not is_voice and start_time is not None:
                if time - start_time > 1.0:  # Minimum 1 second segments
                    voice_segments.append((start_time, time, "speaker"))
                start_time = None
        
        print(f"🎤 Found {len(voice_segments)} voice segments")
        return voice_segments
    
    def extract_voice_segments(self, audio_path: str, segments: List[Tuple[float, float, str]]) -> List[str]:
        """Extract individual voice segments from the audio."""
        print(f"🎵 Extracting voice segments...")
        
        y, sr = librosa.load(audio_path, sr=None)
        extracted_paths = []
        
        for i, (start_time, end_time, speaker) in enumerate(segments):
            start_sample = int(start_time * sr)
            end_sample = int(end_time * sr)
            
            segment_audio = y[start_sample:end_sample]
            
            # Skip very short segments
            if len(segment_audio) < sr * 0.5:  # Less than 0.5 seconds
                continue
            
            # Apply basic noise reduction
            segment_audio = self.basic_noise_reduction(segment_audio, sr)
            
            # Save segment
            output_path = self.output_dir / "final_clips" / f"fischer_voice_{i:03d}.wav"
            sf.write(output_path, segment_audio, sr)
            extracted_paths.append(str(output_path))
        
        print(f"💎 Extracted {len(extracted_paths)} clean voice segments")
        return extracted_paths
    
    def basic_noise_reduction(self, audio: np.ndarray, sr: int) -> np.ndarray:
        """Apply basic noise reduction techniques."""
        # Simple high-pass filter to remove low-frequency noise
        from scipy.signal import butter, filtfilt
        
        # High-pass filter at 80 Hz
        nyquist = sr / 2
        low_cutoff = 80 / nyquist
        b, a = butter(4, low_cutoff, btype='high')
        filtered_audio = filtfilt(b, a, audio)
        
        # Normalize audio
        filtered_audio = filtered_audio / np.max(np.abs(filtered_audio))
        
        return filtered_audio
    
    def quality_filter(self, segment_paths: List[str], min_duration: float = 2.0, 
                      max_duration: float = 10.0) -> List[str]:
        """Filter segments based on quality criteria."""
        print(f"🔍 Filtering segments for quality...")
        
        quality_segments = []
        
        for path in segment_paths:
            try:
                y, sr = librosa.load(path, sr=None)
                duration = len(y) / sr
                
                # Duration filter
                if duration < min_duration or duration > max_duration:
                    continue
                
                # SNR estimate (simple)
                rms = np.sqrt(np.mean(y**2))
                if rms < 0.01:  # Too quiet
                    continue
                
                # Check for clipping
                if np.max(np.abs(y)) > 0.95:
                    continue
                
                quality_segments.append(path)
                
            except Exception as e:
                print(f"⚠️ Error processing {path}: {e}")
                continue
        
        print(f"✨ {len(quality_segments)} high-quality segments selected")
        return quality_segments
    
    def create_training_dataset(self, segment_paths: List[str]) -> str:
        """Organize segments into a training dataset format."""
        print(f"📚 Creating training dataset...")
        
        dataset_dir = self.output_dir / "training_dataset"
        dataset_dir.mkdir(exist_ok=True)
        
        # Copy and rename files with consistent naming
        final_segments = []
        for i, path in enumerate(segment_paths):
            new_name = f"bobby_fischer_{i:04d}.wav"
            new_path = dataset_dir / new_name
            
            # Copy file
            import shutil
            shutil.copy2(path, new_path)
            final_segments.append(str(new_path))
        
        # Create metadata file
        metadata_path = dataset_dir / "metadata.json"
        metadata = {
            "speaker": "Bobby Fischer",
            "total_clips": len(final_segments),
            "source": "Interview extraction",
            "clips": [{"filename": Path(p).name, "path": p} for p in final_segments]
        }
        
        with open(metadata_path, 'w') as f:
            json.dump(metadata, f, indent=2)
        
        print(f"🎯 Training dataset ready!")
        print(f"📁 Location: {dataset_dir}")
        print(f"🎵 Total clips: {len(final_segments)}")
        
        return str(dataset_dir)
    
    def process_youtube_video(self, url: str, speaker_name: str = "fischer") -> str:
        """Complete processing pipeline for YouTube video."""
        print(f"🚀 Starting complete voice extraction pipeline...")
        print(f"🎯 Target speaker: {speaker_name}")
        print(f"🔗 Source URL: {url}")
        print("=" * 60)
        
        try:
            # Step 1: Download audio
            audio_path = self.download_youtube_audio(url, speaker_name)
            
            # Step 2: Analyze audio properties
            properties = self.analyze_audio_properties(audio_path)
            
            # Step 3: Segment by silence
            segments = self.segment_by_silence(audio_path)
            
            # Step 4: Speaker analysis (basic)
            voice_segments = self.speaker_diarization_simple(audio_path)
            
            # Step 5: Extract voice segments
            extracted_paths = self.extract_voice_segments(audio_path, voice_segments)
            
            # Step 6: Quality filtering
            quality_paths = self.quality_filter(extracted_paths)
            
            # Step 7: Create training dataset
            dataset_path = self.create_training_dataset(quality_paths)
            
            print("=" * 60)
            print("🎉 Voice extraction completed successfully!")
            print(f"📊 Final dataset: {len(quality_paths)} high-quality voice clips")
            print(f"📁 Dataset location: {dataset_path}")
            print("=" * 60)
            
            return dataset_path
            
        except Exception as e:
            print(f"❌ Error in processing pipeline: {e}")
            raise


def install_requirements():
    """Install required packages."""
    requirements = [
        "yt-dlp",
        "librosa",
        "soundfile",
        "pydub",
        "scipy",
        "matplotlib",
        "numpy"
    ]
    
    print("📦 Installing required packages...")
    for package in requirements:
        try:
            subprocess.run([sys.executable, "-m", "pip", "install", package], 
                         check=True, capture_output=True)
            print(f"✅ {package} installed")
        except subprocess.CalledProcessError:
            print(f"⚠️ Could not install {package}, you may need to install manually")


def main():
    """Main execution function."""
    print("🎭 Bobby Fischer Voice Extraction Tool")
    print("=====================================")
    
    # YouTube URL for the Fischer interview
    fischer_url = "https://www.youtube.com/watch?v=XzlObDyzcyU"
    
    # Initialize extractor
    extractor = VoiceExtractor("bobby_fischer_voice_extraction")
    
    try:
        # Process the video
        dataset_path = extractor.process_youtube_video(fischer_url, "bobby_fischer")
        
        print("\n🎯 Next Steps for ElevenLabs:")
        print("1. Review the extracted clips in the training_dataset folder")
        print("2. Listen to each clip and remove any that aren't clearly Fischer")
        print("3. Ensure clips are between 2-10 seconds for optimal training")
        print("4. Upload the high-quality clips to ElevenLabs for voice cloning")
        print("5. Train your Bobby Fischer voice model!")
        
        print(f"\n📁 Your training data is ready at: {dataset_path}")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        print("💡 Make sure you have yt-dlp installed: pip install yt-dlp")


if __name__ == "__main__":
    # Uncomment the next line if you need to install requirements
    # install_requirements()
    
    main()