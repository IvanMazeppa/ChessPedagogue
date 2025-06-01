#!/bin/bash

# Project File Chunker for Claude - Bash Version
# This script creates chunks of project files for efficient token usage

OUTPUT_DIR="claude_chunks"
CHUNK_SIZE=50000
CHUNK_COUNT=1

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Function to check if file should be included
should_include() {
    local file="$1"
    
    # Skip binary and media files
    case "${file##*.}" in
        apk|jar|so|dylib|exe|dll|png|jpg|jpeg|gif|ico|svg|mp3|mp4|wav|avi|mov|zip|tar|gz|rar|7z|class|dex|o|pyc)
            return 1
            ;;
    esac
    
    # Skip build directories
    case "$file" in
        */build/*|*/.gradle/*|*/.idea/*|*/gradle/*|*/.git/*|*/node_modules/*|*/__pycache__/*)
            return 1
            ;;
    esac
    
    # Include only source files
    case "${file##*.}" in
        java|kt|xml|gradle|properties|json|md|txt|yml|yaml|cpp|h|c|cc|hpp)
            return 0
            ;;
        *)
            return 1
            ;;
    esac
}

# Initialize first chunk
CURRENT_CHUNK_FILE="$OUTPUT_DIR/chunk_$(printf "%03d" $CHUNK_COUNT).txt"
CURRENT_SIZE=0

echo "# CHUNK $CHUNK_COUNT" > "$CURRENT_CHUNK_FILE"
echo "# Generated on $(date)" >> "$CURRENT_CHUNK_FILE"
echo "#################################################################" >> "$CURRENT_CHUNK_FILE"
echo "" >> "$CURRENT_CHUNK_FILE"

# Create file list
echo "Creating file list..."
> "$OUTPUT_DIR/file_list.txt"

# Find all relevant files
find . -type f | while read -r file; do
    if should_include "$file"; then
        echo "$file" >> "$OUTPUT_DIR/file_list.txt"
    fi
done

# Process each file
echo "Processing files..."
while IFS= read -r file; do
    # Skip if file doesn't exist
    [ -f "$file" ] || continue
    
    # Get file size in characters (approximate)
    FILE_SIZE=$(wc -c < "$file" 2>/dev/null || echo 0)
    
    # If file is too large for current chunk, start new chunk
    if [ $((CURRENT_SIZE + FILE_SIZE)) -gt $CHUNK_SIZE ] && [ $CURRENT_SIZE -gt 0 ]; then
        CHUNK_COUNT=$((CHUNK_COUNT + 1))
        CURRENT_CHUNK_FILE="$OUTPUT_DIR/chunk_$(printf "%03d" $CHUNK_COUNT).txt"
        CURRENT_SIZE=0
        
        echo "# CHUNK $CHUNK_COUNT" > "$CURRENT_CHUNK_FILE"
        echo "# Generated on $(date)" >> "$CURRENT_CHUNK_FILE"
        echo "#################################################################" >> "$CURRENT_CHUNK_FILE"
        echo "" >> "$CURRENT_CHUNK_FILE"
    fi
    
    # Add file to chunk
    echo "" >> "$CURRENT_CHUNK_FILE"
    echo "=================================================================================" >> "$CURRENT_CHUNK_FILE"
    echo "FILE: $file" >> "$CURRENT_CHUNK_FILE"
    echo "=================================================================================" >> "$CURRENT_CHUNK_FILE"
    echo "" >> "$CURRENT_CHUNK_FILE"
    
    # Add file content (handle encoding issues)
    if file -b --mime-encoding "$file" | grep -q "binary"; then
        echo "[Binary file - content not included]" >> "$CURRENT_CHUNK_FILE"
    else
        cat "$file" >> "$CURRENT_CHUNK_FILE" 2>/dev/null || echo "[Error reading file]" >> "$CURRENT_CHUNK_FILE"
    fi
    
    echo "" >> "$CURRENT_CHUNK_FILE"
    echo "" >> "$CURRENT_CHUNK_FILE"
    
    CURRENT_SIZE=$((CURRENT_SIZE + FILE_SIZE))
    
done < "$OUTPUT_DIR/file_list.txt"

# Create project structure
echo "Creating project structure..."
tree -I 'build|.gradle|.idea|gradle|.git|node_modules|__pycache__|*.apk|*.jar' > "$OUTPUT_DIR/project_structure.txt" 2>/dev/null || {
    # Fallback if tree command not available
    find . -type d | grep -v -E '(build|\.gradle|\.idea|gradle|\.git|node_modules|__pycache__)' | sort > "$OUTPUT_DIR/project_structure.txt"
}

# Create summary
echo "Creating summary..."
cat > "$OUTPUT_DIR/README.txt" << EOF
HOW TO USE THESE CHUNKS WITH CLAUDE
=====================================

1. Start a new conversation with Claude
2. Upload chunk files in order (chunk_001.txt, chunk_002.txt, etc.)
3. You can upload multiple chunks at once if they fit within token limits

GENERATED FILES:
- chunk_XXX.txt: Code chunks (upload these to Claude)
- project_structure.txt: Directory structure of the project
- file_list.txt: Complete list of included files
- README.txt: This file

Total chunks created: $CHUNK_COUNT
Generated on: $(date)
EOF

echo ""
echo "✅ Complete!"
echo "📁 Output directory: $OUTPUT_DIR"
echo "📄 Total chunks created: $CHUNK_COUNT"
echo ""
echo "To use with Claude:"
echo "1. Navigate to: cd $OUTPUT_DIR"
echo "2. Upload the chunk_XXX.txt files to Claude"