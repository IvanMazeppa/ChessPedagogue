from openai import OpenAI
import os

# Create a client object
client = OpenAI(api_key="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")  # Replace with your actual API key

# The file path to your JSONL file
file_path = "chess_masters_combined_10plus.jsonl"

print(f"Preparing to upload {file_path}...")
print(f"File size: {os.path.getsize(file_path)} bytes")
print(f"Checking file format...")

# Quick validation - count examples
example_count = 0
with open(file_path, 'r') as f:
    for line in f:
        example_count += 1
print(f"Found {example_count} examples in the file.")

try:
    # Upload with verbose output
    print("\nStarting upload to OpenAI...")
    
    with open(file_path, "rb") as file:
        response = client.files.create(
            file=file,
            purpose="fine-tune"
        )
    
    # Get and display file ID
    file_id = response.id
    print(f"\n✓ Success! File uploaded with ID: {file_id}")
    print(f"File status: {response.status}")
    
    # Save to a file for reference
    with open("upload_success.txt", "w") as details_file:
        details_file.write(f"File ID: {file_id}\n")
        details_file.write(f"Status: {response.status}\n")
    print("Details saved to upload_success.txt")
    
except Exception as e:
    print(f"\n⚠ Error during upload: {e}")