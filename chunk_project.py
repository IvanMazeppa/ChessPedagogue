#!/usr/bin/env python3
"""
Project File Chunker for Claude
This script creates optimized chunks of project files for efficient token usage when sharing with Claude.
"""

import os
import sys
import argparse
import json
from pathlib import Path
from typing import List, Dict, Tuple
import mimetypes

class ProjectChunker:
    def __init__(self, project_root: str, output_dir: str = "claude_chunks"):
        self.project_root = Path(project_root)
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(exist_ok=True)
        
        # File extensions to include
        self.include_extensions = {
            '.java', '.kt', '.xml', '.gradle', '.properties', 
            '.json', '.md', '.txt', '.yml', '.yaml',
            '.cpp', '.h', '.c', '.cc', '.hpp'
        }
        
        # Directories to skip
        self.skip_dirs = {
            'build', '.gradle', '.idea', 'gradle', '.git',
            'app/build', 'captures', '.externalNativeBuild',
            'node_modules', '__pycache__', '.pytest_cache'
        }
        
        # Binary/large file extensions to skip
        self.skip_extensions = {
            '.apk', '.jar', '.so', '.dylib', '.exe', '.dll',
            '.png', '.jpg', '.jpeg', '.gif', '.ico', '.svg',
            '.mp3', '.mp4', '.wav', '.avi', '.mov',
            '.zip', '.tar', '.gz', '.rar', '.7z',
            '.class', '.dex', '.o', '.pyc'
        }
        
        self.files_metadata = []
        self.chunk_size_limit = 50000  # characters per chunk
    
    def should_include_file(self, file_path: Path) -> bool:
        """Check if file should be included based on extension and path."""
        # Skip if in excluded directory
        for skip_dir in self.skip_dirs:
            if skip_dir in str(file_path):
                return False
        
        # Skip binary/large files
        if file_path.suffix.lower() in self.skip_extensions:
            return False
        
        # Include only specified extensions
        return file_path.suffix.lower() in self.include_extensions
    
    def get_file_info(self, file_path: Path) -> Dict:
        """Get metadata about a file."""
        try:
            stats = file_path.stat()
            relative_path = file_path.relative_to(self.project_root)
            
            # Try to read file content
            content = ""
            char_count = 0
            line_count = 0
            
            try:
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                    char_count = len(content)
                    line_count = content.count('\n') + 1
            except Exception as e:
                content = f"[Error reading file: {e}]"
            
            return {
                'path': str(relative_path),
                'size_bytes': stats.st_size,
                'char_count': char_count,
                'line_count': line_count,
                'content': content
            }
        except Exception as e:
            return {
                'path': str(file_path),
                'error': str(e)
            }
    
    def scan_project(self) -> List[Dict]:
        """Scan project and collect all relevant files."""
        files = []
        
        for root, dirs, filenames in os.walk(self.project_root):
            # Remove directories we want to skip
            dirs[:] = [d for d in dirs if d not in self.skip_dirs]
            
            for filename in filenames:
                file_path = Path(root) / filename
                if self.should_include_file(file_path):
                    file_info = self.get_file_info(file_path)
                    if 'error' not in file_info:
                        files.append(file_info)
        
        return sorted(files, key=lambda x: x['path'])
    
    def create_chunks(self, files: List[Dict]) -> List[Dict]:
        """Create optimized chunks of files."""
        chunks = []
        current_chunk = {
            'files': [],
            'total_chars': 0,
            'file_count': 0
        }
        
        # Group files by directory for better context
        from collections import defaultdict
        by_directory = defaultdict(list)
        
        for file in files:
            dir_path = os.path.dirname(file['path'])
            by_directory[dir_path].append(file)
        
        # Process files directory by directory
        for dir_path in sorted(by_directory.keys()):
            dir_files = by_directory[dir_path]
            
            for file in dir_files:
                file_chars = file['char_count']
                
                # If single file is too large, create its own chunk
                if file_chars > self.chunk_size_limit:
                    # Save current chunk if it has content
                    if current_chunk['files']:
                        chunks.append(current_chunk)
                    
                    # Create chunk for large file
                    chunks.append({
                        'files': [file],
                        'total_chars': file_chars,
                        'file_count': 1,
                        'note': 'Large file - standalone chunk'
                    })
                    
                    # Reset current chunk
                    current_chunk = {
                        'files': [],
                        'total_chars': 0,
                        'file_count': 0
                    }
                # If adding file would exceed limit, start new chunk
                elif current_chunk['total_chars'] + file_chars > self.chunk_size_limit:
                    chunks.append(current_chunk)
                    current_chunk = {
                        'files': [file],
                        'total_chars': file_chars,
                        'file_count': 1
                    }
                # Add file to current chunk
                else:
                    current_chunk['files'].append(file)
                    current_chunk['total_chars'] += file_chars
                    current_chunk['file_count'] += 1
        
        # Don't forget the last chunk
        if current_chunk['files']:
            chunks.append(current_chunk)
        
        return chunks
    
    def write_chunk_files(self, chunks: List[Dict]):
        """Write chunks to files."""
        # Write summary file
        summary = {
            'total_files': sum(c['file_count'] for c in chunks),
            'total_chunks': len(chunks),
            'chunk_size_limit': self.chunk_size_limit,
            'chunks': []
        }
        
        for i, chunk in enumerate(chunks):
            chunk_filename = f"chunk_{i+1:03d}.txt"
            chunk_path = self.output_dir / chunk_filename
            
            # Write chunk content
            with open(chunk_path, 'w', encoding='utf-8') as f:
                f.write(f"# CHUNK {i+1} of {len(chunks)}\n")
                f.write(f"# Files: {chunk['file_count']}, Characters: {chunk['total_chars']:,}\n")
                if 'note' in chunk:
                    f.write(f"# Note: {chunk['note']}\n")
                f.write("#" * 80 + "\n\n")
                
                for file in chunk['files']:
                    f.write(f"\n{'='*80}\n")
                    f.write(f"FILE: {file['path']}\n")
                    f.write(f"Lines: {file['line_count']}, Characters: {file['char_count']:,}\n")
                    f.write(f"{'='*80}\n\n")
                    f.write(file['content'])
                    f.write("\n\n")
            
            # Add to summary
            summary['chunks'].append({
                'filename': chunk_filename,
                'file_count': chunk['file_count'],
                'total_chars': chunk['total_chars'],
                'files': [f['path'] for f in chunk['files']]
            })
        
        # Write summary
        summary_path = self.output_dir / "chunks_summary.json"
        with open(summary_path, 'w', encoding='utf-8') as f:
            json.dump(summary, f, indent=2)
        
        # Write usage instructions
        instructions_path = self.output_dir / "README.txt"
        with open(instructions_path, 'w', encoding='utf-8') as f:
            f.write("HOW TO USE THESE CHUNKS WITH CLAUDE\n")
            f.write("="*40 + "\n\n")
            f.write("1. Start a new conversation with Claude\n")
            f.write("2. Upload chunk files in order (chunk_001.txt, chunk_002.txt, etc.)\n")
            f.write("3. You can upload multiple chunks at once if they fit within token limits\n")
            f.write("4. Reference chunks_summary.json to see which files are in each chunk\n\n")
            f.write("TIPS:\n")
            f.write("- Upload only the chunks relevant to your question\n")
            f.write("- Start with chunk_001.txt which usually contains important base files\n")
            f.write("- Use chunks_summary.json to find specific files quickly\n\n")
            f.write(f"Total files: {summary['total_files']}\n")
            f.write(f"Total chunks: {summary['total_chunks']}\n")
            f.write(f"Output directory: {self.output_dir.absolute()}\n")
    
    def generate_file_tree(self) -> str:
        """Generate a tree view of the project structure."""
        tree_lines = ["PROJECT STRUCTURE\n" + "="*40 + "\n"]
        
        def add_tree(path: Path, prefix: str = "", is_last: bool = True):
            if path.name in self.skip_dirs:
                return
            
            # Skip hidden directories
            if path.name.startswith('.') and path.name not in ['.gitignore', '.env']:
                return
            
            connector = "└── " if is_last else "├── "
            tree_lines.append(prefix + connector + path.name)
            
            if path.is_dir():
                extension = "    " if is_last else "│   "
                try:
                    children = sorted(list(path.iterdir()), 
                                    key=lambda x: (not x.is_dir(), x.name.lower()))
                    for i, child in enumerate(children):
                        is_last_child = i == len(children) - 1
                        add_tree(child, prefix + extension, is_last_child)
                except PermissionError:
                    pass
        
        add_tree(self.project_root)
        return "\n".join(tree_lines)
    
    def run(self):
        """Execute the chunking process."""
        print(f"Scanning project at: {self.project_root}")
        files = self.scan_project()
        print(f"Found {len(files)} relevant files")
        
        if not files:
            print("No files found to chunk!")
            return
        
        print("Creating optimized chunks...")
        chunks = self.create_chunks(files)
        print(f"Created {len(chunks)} chunks")
        
        print(f"Writing chunks to: {self.output_dir.absolute()}")
        self.write_chunk_files(chunks)
        
        # Generate and save file tree
        tree = self.generate_file_tree()
        tree_path = self.output_dir / "project_structure.txt"
        with open(tree_path, 'w', encoding='utf-8') as f:
            f.write(tree)
        
        print("\nComplete! Check the output directory for:")
        print("  - chunk_XXX.txt files (the actual code chunks)")
        print("  - chunks_summary.json (index of all chunks)")
        print("  - project_structure.txt (directory tree)")
        print("  - README.txt (usage instructions)")


def main():
    parser = argparse.ArgumentParser(description="Chunk project files for efficient Claude sharing")
    parser.add_argument("project_path", nargs="?", default=".", 
                       help="Path to project root (default: current directory)")
    parser.add_argument("-o", "--output", default="claude_chunks",
                       help="Output directory name (default: claude_chunks)")
    parser.add_argument("-s", "--size", type=int, default=50000,
                       help="Maximum characters per chunk (default: 50000)")
    
    args = parser.parse_args()
    
    chunker = ProjectChunker(args.project_path, args.output)
    chunker.chunk_size_limit = args.size
    chunker.run()


if __name__ == "__main__":
    main()