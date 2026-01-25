#!/bin/bash

# Script to switch between embedding providers

set -e

PROVIDER=${1:-ollama}

echo "🔧 Switching to $PROVIDER embedding provider..."

if [ "$PROVIDER" = "ollama" ]; then
    # Update build.gradle to use Ollama
    sed -i 's/spring-ai-starter-model-openai/spring-ai-ollama-spring-boot-starter/' build.gradle

    # Update application.yml to use Ollama
    sed -i 's/spring-ai-starter-model-openai/spring-ai-ollama-spring-boot-starter/' build.gradle

    echo "✅ Switched to Ollama embeddings"
    echo "   - Uses nomic-embed-text model (free, local)"
    echo "   - Dimensions: 768"
    echo ""
    echo "📝 Make sure Ollama is running on localhost:11434"

elif [ "$PROVIDER" = "openai" ]; then
    # Update build.gradle to use OpenAI
    sed -i 's/spring-ai-ollama-spring-boot-starter/spring-ai-starter-model-openai/' build.gradle

    echo "✅ Switched to OpenAI embeddings"
    echo "   - Uses text-embedding-3-small model"
    echo "   - Dimensions: 1536"
    echo ""
    echo "📝 Make sure OPENAI_API_KEY is set"

else
    echo "❌ Unknown provider: $PROVIDER"
    echo "   Available options: ollama, openai"
    exit 1
fi

echo ""
echo "🔄 Rebuilding the application..."
./gradlew clean build -x test

echo "✅ Done! You can now run the application with ./gradlew bootRun"