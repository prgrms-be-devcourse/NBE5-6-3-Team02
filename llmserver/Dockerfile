# Base image
FROM python:3.11-slim

# Working directory
WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

RUN python -m spacy download en_core_web_sm

# Copy app code
COPY app ./app

# Set env vars (OpenAI API key should be set later via Cloud Run)
ENV PYTHONUNBUFFERED=1

# Expose port and run app
CMD ["sh", "-c", "cd app && uvicorn main:app --host 0.0.0.0 --port 8080"]