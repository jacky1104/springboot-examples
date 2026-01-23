package com.example.rag.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    private static final Logger logger = LoggerFactory.getLogger(VectorSearchService.class);

    @Autowired
    private VectorStore vectorStore;

    @Value("${rag.retrieval.max-results:5}")
    private int maxResults;

    @Value("${rag.retrieval.min-score:0.7}")
    private double minScore;

    public List<Document> search(String query) {
        logger.info("Searching for documents related to: {}", query);

        // For Spring AI 1.1.2, use similaritySearch without limit parameter
        List<Document> results = vectorStore.similaritySearch(query);

        // Limit results manually
        if (results.size() > maxResults) {
            results = results.subList(0, maxResults);
        }

        logger.info("Found {} relevant documents", results.size());
        return results;
    }

    public String buildContextFromDocuments(List<Document> documents) {
        if (documents.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("Relevant information from documents:\n\n");

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            context.append("[Document ").append(i + 1).append("]")
                    .append(" (File: ").append(doc.getMetadata().getOrDefault("fileName", "Unknown"))
                    .append(")\n");
            context.append(doc.getText()).append("\n\n");
        }

        return context.toString();
    }

    public List<String> extractFileNames(List<Document> documents) {
        return documents.stream()
                .map(doc -> (String) doc.getMetadata().getOrDefault("fileName", "Unknown"))
                .distinct()
                .collect(Collectors.toList());
    }
}