-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS vector_store;

-- Grant permissions
GRANT ALL ON SCHEMA vector_store TO rag_user;
GRANT ALL ON ALL TABLES IN SCHEMA vector_store TO rag_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA vector_store TO rag_user;