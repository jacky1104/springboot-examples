# Spring AI RAG System with pgvector

A Spring Boot application that implements a Retrieval-Augmented Generation (RAG) system using pgvector as the vector database. The system allows you to upload documents (PDF, TXT, DOCX) and ask questions about them using AI.

## Features

- Document upload and processing (PDF, TXT, DOCX)
- Text chunking with configurable overlap
- Vector embeddings using OpenAI's text-embedding models
- Vector similarity search using pgvector
- RAG implementation with Anthropic Claude
- RESTful API for document management and Q&A

## Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Anthropic API key (for Claude model)

## Quick Start

### Option 1: Local Embeddings (No External Dependencies)

This is the default option using a lightweight local embedding implementation.

### 1. Start pgvector Database

```bash
cd docker
docker-compose -f docker-compose-local.yml up -d
```

### 2. Set Environment Variable

```bash
export ANTHROPIC_API_KEY="your-anthropic-api-key"
```

### 3. Build and Run the Application

```bash
./gradlew bootRun
```

### Option 2: Using Ollama (Free, Local Embeddings)

If you prefer Ollama for better embedding quality.

```bash
cd docker
docker-compose -f docker-compose-ollama.yml up -d
```

### Option 3: Using OpenAI (Paid API)

If you have an OpenAI API key and prefer their embeddings.

```bash
cd docker
docker-compose up -d
```

### 2. Set Environment Variables

```bash
export OPENAI_API_KEY="your-openai-api-key"
export ANTHROPIC_API_KEY="your-anthropic-api-key"
```

### 3. Build and Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## API Endpoints

### Document Management

- **Upload Document**
  ```
  POST /api/documents/upload
  Content-Type: multipart/form-data

  Body: file (multipart file)
  ```

- **List All Documents**
  ```
  GET /api/documents
  ```

- **Get Document Details**
  ```
  GET /api/documents/{id}
  ```

- **Delete Document**
  ```
  DELETE /api/documents/{id}
  ```

- **Get Document Statistics**
  ```
  GET /api/documents/stats
  ```

### Q&A

- **Ask a Question**
  ```
  POST /api/qa/ask
  Content-Type: application/json

  Body: {
    "question": "Your question here"
  }
  ```

## Configuration

The application can be configured through `application.yml`:

### Embedding Options

1. **Local Embeddings (Default)** - No external dependencies:
   - Uses a lightweight TF-IDF based implementation
   - Works entirely offline
   - Good for development and testing

2. **Ollama** - Free, local embeddings:
   ```yaml
   spring:
     ai:
       ollama:
         base-url: http://localhost:11434
         embedding:
           model: nomic-embed-text
           options:
             dimensions: 768
   ```

3. **OpenAI** - If you have an API key:
   ```yaml
   spring:
     ai:
       openai:
         api-key: ${OPENAI_API_KEY}
         embedding:
           options:
             model: text-embedding-3-small
             dimensions: 1536
   ```

### Other Settings

- **File Storage**: Files are stored in `/home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf`
- **Chunking**: Adjust `max-chunk-size` and `chunk-overlap`
- **Retrieval**: Configure `max-results` and `min-score` for vector search
- **AI Models**: Change Claude model and parameters
- **Database**: Update PostgreSQL connection settings

### File Storage Configuration

By default, uploaded documents are stored in `/home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf`. To change this location, update the following in `application.yml`:

```yaml
rag:
  file-storage:
    path: /your/custom/path
```

When using Docker, make sure to update the volume mounts in the docker-compose files accordingly.

## Example Usage

### Upload a Document

```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@document.pdf"
```

### Ask a Question

```bash
curl -X POST http://localhost:8080/api/qa/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What is the main topic of the document?"}'
```

## Project Structure

```
spring-ai-rag/
├── src/main/java/com/example/rag/
│   ├── controller/          # REST controllers
│   ├── entity/             # JPA entities
│   ├── repository/         # Data repositories
│   ├── service/            # Business logic services
│   └── RagApplication.java # Main application class
├── src/main/resources/
│   └── application.yml     # Configuration
├── docker/
│   ├── docker-compose.yml  # pgvector setup
│   └── init.sql           # Database initialization
└── build.gradle           # Gradle build configuration
```

## How It Works

1. **Document Upload**: Files are uploaded and stored locally
2. **Text Extraction**: Apache Tika extracts text from documents
3. **Text Chunking**: Documents are split into manageable chunks
4. **Embedding Generation**: OpenAI creates vector embeddings for each chunk
5. **Vector Storage**: Embeddings are stored in pgvector database
6. **Question Processing**: User questions are embedded and used for similarity search
7. **Context Retrieval**: Most relevant chunks are retrieved based on vector similarity
8. **Answer Generation**: Claude uses retrieved context to generate answers

## Development

### Running Tests

```bash
./gradlew test
```

### Building the Application

```bash
./gradlew build
```

### Docker Build

```bash
docker build -t spring-ai-rag .
```

## Troubleshooting

- Ensure Docker is running before starting the application
- Check that API keys are properly set
- Verify pgvector is running on port 5432
- Check application logs for detailed error messages

## License

This project is open source and available under the MIT License.