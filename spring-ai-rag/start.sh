#!/bin/bash

# Spring AI RAG Quick Start Script

set -e

echo "🚀 Starting Spring AI RAG System..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check environment variables
if [ -z "$ANTHROPIC_API_KEY" ]; then
    echo "⚠️  Warning: ANTHROPIC_API_KEY is not set. Please set it:"
    echo "   export ANTHROPIC_API_KEY=your-anthropic-api-key"
    echo ""
    echo "❗ Note: You only need Anthropic API key. OpenAI is optional (Ollama is used by default for embeddings)."
fi

# Start pgvector (local embeddings - no external dependencies)
echo "📦 Starting pgvector database (local embeddings)..."
cd docker
docker-compose -f docker-compose-local.yml up -d

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
sleep 10

# Check if database is ready
until docker exec pgvector-rag pg_isready -U rag_user -d rag_db; do
    echo "⏳ Waiting for database..."
    sleep 2
done

echo "✅ Database is ready!"

# Go back to project root
cd ..

# Build and run the application
echo "🔨 Building and running the application..."
./gradlew bootRun

echo "✅ Application is running on http://localhost:8080"