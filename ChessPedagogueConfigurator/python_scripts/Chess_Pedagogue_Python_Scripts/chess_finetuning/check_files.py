from openai import OpenAI

# Create a client object with your API key
client = OpenAI(api_key="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")  # Replace with your actual key

# List all uploaded files
files = client.files.list()

# Print file details
print("Your uploaded files:")
for file in files.data:
    print(f"ID: {file.id} | Filename: {file.filename} | Purpose: {file.purpose} | Status: {file.status}")

print(f"\nTotal files found: {len(files.data)}")