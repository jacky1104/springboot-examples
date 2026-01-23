package com.example.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class AnsibleInventoryChunkingService {

    @Value("${rag.chunking.ansible.section-size:50}")
    private int maxSectionSize;

    @Value("${rag.chunking.ansible.include-metadata:true}")
    private boolean includeMetadata;

    /**
     * Chunk Ansible inventory files by sections while preserving structure
     */
    public List<String> chunkAnsibleInventory(String content, String filePath) {
        if (content == null || content.trim().isEmpty()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();
        String environment = extractEnvironmentFromPath(filePath);

        // Split by sections (lines starting with [])
        Pattern sectionPattern = Pattern.compile("^\\[([^\\]]+)\\]", Pattern.MULTILINE);
        Matcher matcher = sectionPattern.matcher(content);

        int lastEnd = 0;
        String currentSection = "header";
        StringBuilder sectionContent = new StringBuilder();

        while (matcher.find()) {
            // Process previous section
            if (sectionContent.length() > 0) {
                String chunk = createChunk(currentSection, sectionContent.toString(), environment);
                chunks.add(chunk);

                // Handle large sections by splitting them
                if (sectionContent.length() > maxSectionSize * 50) { // rough character count
                    chunks.addAll(splitLargeSection(currentSection, sectionContent.toString(), environment));
                }
            }

            // Start new section
            currentSection = matcher.group(1);
            sectionContent = new StringBuilder();
            sectionContent.append(matcher.group()).append("\n");
            lastEnd = matcher.end();
        }

        // Add remaining content
        if (lastEnd < content.length()) {
            sectionContent.append(content.substring(lastEnd));
        }

        if (sectionContent.length() > 0) {
            String chunk = createChunk(currentSection, sectionContent.toString(), environment);
            chunks.add(chunk);
        }

        return chunks;
    }

    private List<String> splitLargeSection(String sectionName, String content, String environment) {
        List<String> subChunks = new ArrayList<>();
        String[] lines = content.split("\n");

        StringBuilder currentChunk = new StringBuilder();
        currentChunk.append("[").append(sectionName).append("]").append("\n");
        int lineCount = 0;

        for (int i = 1; i < lines.length; i++) { // Skip section header
            String line = lines[i];

            // Don't split in middle of a host definition
            if (lineCount >= maxSectionSize && !line.startsWith(" ") && !line.isEmpty()) {
                String chunk = createChunk(sectionName, currentChunk.toString(), environment);
                subChunks.add(chunk);

                currentChunk = new StringBuilder();
                currentChunk.append("[").append(sectionName).append("]").append("\n");
                lineCount = 0;
            }

            currentChunk.append(line).append("\n");
            lineCount++;
        }

        if (currentChunk.length() > (sectionName.length() + 3)) {
            String chunk = createChunk(sectionName, currentChunk.toString(), environment);
            subChunks.add(chunk);
        }

        return subChunks;
    }

    private String createChunk(String sectionName, String content, String environment) {
        if (!includeMetadata || environment == null || environment.isEmpty()) {
            return content;
        }

        StringBuilder chunk = new StringBuilder();
        chunk.append("Environment: ").append(environment).append("\n");
        chunk.append("File: serverHosts").append("\n");
        chunk.append("Section: ").append(sectionName).append("\n");
        chunk.append("Type: Ansible Inventory").append("\n");
        chunk.append("---\n");
        chunk.append(content);

        return chunk.toString();
    }

    private String extractEnvironmentFromPath(String filePath) {
        // Extract environment name from path like /path/to/devqagl/serverHosts
        if (filePath == null || filePath.isEmpty()) {
            return "unknown";
        }

        // Remove file name and get parent directory
        String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
        String envName = dirPath.substring(dirPath.lastIndexOf('/') + 1);

        // Normalize environment names
        if (envName.toLowerCase().contains("devqa")) {
            if (envName.toLowerCase().contains("global")) {
                return "devqagl";
            } else if (envName.toLowerCase().contains("europe")) {
                return "devqaeu";
            } else if (envName.toLowerCase().contains("us")) {
                return "devqaus";
            } else if (envName.toLowerCase().contains("japan")) {
                return "devqajp";
            }
        }

        return envName;
    }
}