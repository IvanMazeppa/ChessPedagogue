from openai import OpenAI
import time

# Create a client
client = OpenAI(api_key="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")  # Your actual API key

# Get the job ID from your file or input
job_id = ""  # Replace with your job ID

# If you saved the job ID, you can read it:
try:
    with open("finetune_job_details.txt", "r") as f:
        for line in f:
            if line.startswith("Job ID:"):
                job_id = line.split("Job ID:")[1].strip()
                break
    if job_id:
        print(f"Found saved job ID: {job_id}")
    else:
        job_id = input("Please enter your job ID: ")
except FileNotFoundError:
    job_id = input("Please enter your job ID: ")

print(f"\nChecking status of fine-tuning job: {job_id}")

try:
    # Retrieve the fine-tuning job
    job = client.fine_tuning.jobs.retrieve(job_id)
    
    print(f"\n✨ Job Status: {job.status}")
    print(f"Model: {job.model}")
    
    if hasattr(job, 'fine_tuned_model') and job.fine_tuned_model:
        print(f"🎉 Fine-tuned model ID: {job.fine_tuned_model}")
        print("\nYour chess masters model is ready to use!")
    
    if job.status == "failed":
        print(f"\n⚠ Job failed. Error message: {job.error}")
    
except Exception as e:
    print(f"\n⚠ Error checking job status: {e}")