import os
import time
from openai import OpenAI

# Initialize the client with your API key
client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))

def upload_and_finetune(filename):
    """Uploads a file and starts fine-tuning"""
    
    print(f"🌟 Preparing to upload {filename} for fine-tuning!")
    
    try:
        # Step 1: Upload the file
        print("\nUploading your training examples...")
        with open(filename, "rb") as file:
            response = client.files.create(
                file=file,
                purpose="fine-tune"
            )
        
        file_id = response.id
        print(f"✓ Success! File uploaded with ID: {file_id}")
        
        # Wait a moment for the file to process
        print("Giving OpenAI a moment to process your file...")
        time.sleep(5)
        
        # Step 2: Create the fine-tuning job with the powerful model
        print("\nStarting the fine-tuning process...")
        job = client.fine_tuning.jobs.create(
            training_file=file_id,
            model="gpt-4.1-2025-04-14"  # Using your powerful model!
        )
        
        job_id = job.id
        print(f"✓ Fine-tuning job created! Job ID: {job_id}")
        
        # Save the details for future reference
        with open("finetune_details.txt", "w") as f:
            f.write(f"File ID: {file_id}\n")
            f.write(f"Job ID: {job_id}\n")
            f.write(f"Started at: {time.strftime('%Y-%m-%d %H:%M:%S')}\n")
        
        print("\n✨ Your fine-tuning job is now running! ✨")
        print("Fine-tuning typically takes 1-4 hours.")
        
        return job_id
        
    except Exception as e:
        print(f"We encountered a challenge: {e}")
        return None

# Run the upload and fine-tuning process
if __name__ == "__main__":
    combined_file = "chess_masters_combined.jsonl"
    upload_and_finetune(combined_file)