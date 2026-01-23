package com.example.rag.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private VectorStore vectorStore;

    @Value("${rag.retrieval.max-results:10}")
    private int maxResults;

    @Value("${rag.retrieval.min-score:0.5}")
    private double minScore;

    public List<List<Double>> generateEmbeddings(List<String> texts) {
        logger.info("Generating embeddings for {} texts", texts.size());

        EmbeddingResponse response = embeddingModel.embedForResponse(texts);

        return response.getResults().stream()
                .map(result -> {
                    float[] floatArray = result.getOutput();
                    List<Double> doubleList = new ArrayList<>();
                    for (float f : floatArray) {
                        doubleList.add((double) f);
                    }
                    return doubleList;
                })
                .collect(Collectors.toList());
    }

    public void storeEmbeddings(List<String> chunks, String documentId, String fileName) {
        logger.info("Storing {} embeddings for document: {}", chunks.size(), documentId);

        List<Document> documents = chunks.stream()
                .map(chunk -> new Document(chunk, Map.of(
                        "documentId", documentId,
                        "fileName", fileName,
                        "chunkIndex", String.valueOf(chunks.indexOf(chunk))
                )))
                .collect(Collectors.toList());

        vectorStore.add(documents);
        logger.info("Successfully stored {} embeddings", documents.size());
    }

    public List<Document> searchSimilar(String query, int topK) {
        logger.info("Searching for top {} similar documents for query: {}", topK, query);

        // For Spring AI 1.1.2, use similaritySearch without limit parameter
        List<Document> results = vectorStore.similaritySearch(query);

        // Filter by minimum score
        results = results.stream()
                .filter(doc -> {
                    // Extract score from metadata if available
                    Object scoreObj = doc.getMetadata().get("score");
                    if (scoreObj instanceof Double) {
                        return (Double) scoreObj >= minScore;
                    }
                    return true; // Include if no score
                })
                .limit(maxResults)
                .collect(Collectors.toList());

        logger.info("Found {} similar documents after filtering", results.size());
        return results;
    }

    public void deleteDocumentEmbeddings(String documentId) {
        logger.info("Deleting embeddings for document: {}", documentId);

        // Note: Spring AI doesn't provide a direct method to delete by metadata
        // This would need to be implemented using custom repository methods
        logger.warn("Document deletion by ID not implemented in current Spring AI version");
    }
}