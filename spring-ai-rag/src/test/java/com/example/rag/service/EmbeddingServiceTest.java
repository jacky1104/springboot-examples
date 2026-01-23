package com.example.rag.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmbeddingServiceTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void testGenerateEmbeddings() {
        List<String> texts = List.of(
                "This is a test document",
                "Another test document with different content"
        );

        List<List<Double>> embeddings = embeddingService.generateEmbeddings(texts);

        assertNotNull(embeddings);
        assertEquals(2, embeddings.size());
        assertFalse(embeddings.get(0).isEmpty());
        assertFalse(embeddings.get(1).isEmpty());
    }
}