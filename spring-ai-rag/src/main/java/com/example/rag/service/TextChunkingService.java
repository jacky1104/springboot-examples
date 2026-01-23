package com.example.rag.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkingService {

    @Value("${rag.chunking.max-chunk-size:1000}")
    private int maxChunkSize;

    @Value("${rag.chunking.chunk-overlap:200}")
    private int chunkOverlap;

    @Autowired
    private AnsibleInventoryChunkingService ansibleInventoryChunkingService;

    public List<String> chunkText(String text) {
        return chunkText(text, null);
    }

    public List<String> chunkText(String text, String fileName) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        // Special handling for serverHosts files
        if (fileName != null && fileName.contains("serverHosts")) {
            return ansibleInventoryChunkingService.chunkAnsibleInventory(text, fileName);
        }

        List<String> chunks = new ArrayList<>();

        // Split by sentences first
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() <= maxChunkSize) {
                currentChunk.append(sentence).append(" ");
            } else {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());

                    // Add overlap for next chunk
                    if (chunkOverlap > 0 && chunks.size() > 0) {
                        String previousChunk = chunks.get(chunks.size() - 1);
                        int overlapStart = Math.max(0, previousChunk.length() - chunkOverlap);
                        currentChunk = new StringBuilder(previousChunk.substring(overlapStart));
                    } else {
                        currentChunk = new StringBuilder();
                    }
                }
                currentChunk.append(sentence).append(" ");
            }
        }

        // Add the last chunk
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        // If no chunks were created (text too short), add the whole text
        if (chunks.isEmpty() && !text.trim().isEmpty()) {
            chunks.add(text.trim());
        }

        return chunks;
    }

    public List<String> chunkTextWithMetadata(String text, String metadata) {
        List<String> chunks = chunkText(text);

        if (metadata != null && !metadata.isEmpty()) {
            // Prepend metadata to each chunk
            return chunks.stream()
                    .map(chunk -> metadata + "\n\n" + chunk)
                    .toList();
        }

        return chunks;
    }
}