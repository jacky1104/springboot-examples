# Spring AI RAG System - Claude.md

## Project Overview

This is a Spring Boot application that implements a Retrieval-Augmented Generation (RAG) system using pgvector as the vector database. The system allows users to upload documents (PDF, TXT, DOCX) and ask questions about them using AI.

## Tech Stack

### Core Framework
- **Spring Boot 3.4.1** - Main application framework
- **Java 21** - Programming language
- **Spring AI 1.1.2** - AI integration framework
- **Spring Data JPA** - Data persistence layer

### AI/ML Components
- **Anthropic Claude** - Primary LLM for answer generation
- **Transformers (sentence-transformers/all-MiniLM-L6-v2)** - Local embedding model
- **Optional: OpenAI** - Alternative embedding provider
- **Optional: Ollama** - Local embedding server

### Database & Storage
- **PostgreSQL with pgvector extension** - Vector database for embeddings
- **Apache Tika** - Document text extraction (PDF, DOCX, TXT)
- **Local file system** - Document storage (/home/jackyxiang/codes/serverconf/FortiGateCloud/deployconf)

### Infrastructure
- **Docker & Docker Compose** - Containerization
- **Gradle** - Build automation
- **JUnit 5** - Testing framework

## Key Features

### Document Management
- Multi-format document upload (PDF, TXT, DOCX)
- Automatic text extraction and chunking
- Document metadata storage
- RESTful API for CRUD operations

### Vector Processing
- Configurable text chunking (max 1000 chars, 200 overlap)
- Vector embedding generation using local transformers
- pgvector storage with HNSW index and cosine similarity
- Configurable retrieval parameters (top 5 results, min score 0.7)

### RAG Implementation
- Semantic search using vector similarity
- Context-aware answer generation
- Integration with Anthropic Claude model
- Configurable AI parameters (temperature: 0.7, max tokens: 1000)

## API Endpoints

### Document Management
- `POST /api/documents/upload` - Upload document
- `GET /api/documents` - List all documents
- `GET /api/documents/{id}` - Get document details
- `DELETE /api/documents/{id}` - Delete document
- `GET /api/documents/stats` - Get document statistics

### Q&A System
- `POST /api/qa/ask` - Ask questions about documents

## Configuration

The application uses `application.yml` for configuration with support for:
- Multiple embedding providers (local, Ollama, OpenAI)
- Configurable chunking and retrieval parameters
- Custom file storage paths
- Database connection settings
- AI model parameters

## Project Structure

```
spring-ai-rag/
├── src/main/java/com/example/rag/
│   ├── controller/          # REST API endpoints
│   ├── entity/             # JPA entities
│   ├── repository/         # Data access layer
│   ├── service/            # Business logic
│   └── RagApplication.java # Main application
├── src/main/resources/
│   └── application.yml     # Configuration
├── docker/                 # Docker configurations
└── build.gradle           # Build configuration
```

## Usage Flow

1. **Document Upload**: Files are uploaded and stored locally
2. **Text Extraction**: Apache Tika extracts text from documents
3. **Chunking**: Documents are split into manageable chunks
4. **Embedding**: Each chunk is converted to a vector embedding
5. **Storage**: Embeddings are stored in pgvector database
6. **Query Processing**: User questions are embedded
7. **Retrieval**: Similar chunks are found using vector search
8. **Answer Generation**: Claude generates answers using retrieved context

## Development

### Quick Start
```bash
# Start pgvector database
cd docker && docker-compose -f docker-compose-local.yml up -d

# Set API key
export ANTHROPIC_API_KEY="your-key"

# Run application
./gradlew bootRun
```

### Testing
```bash
./gradlew test
```

### Building
```bash
./gradlew build
```

## Key Dependencies

- `spring-ai-starter-model-anthropic` - Claude integration
- `spring-ai-transformers` - Local embedding models
- `spring-ai-pgvector-store` - Vector database integration
- `spring-ai-tika-document-reader` - Document processing
- `postgresql` - Database driver
- `tika-core & tika-parsers` - Document parsing