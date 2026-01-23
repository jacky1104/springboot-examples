package com.example.rag.service;

import com.example.rag.entity.Document;
import com.example.rag.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentManagementService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentManagementService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private DocumentParsingService documentParsingService;

    @Autowired
    private TextChunkingService textChunkingService;

    @Autowired
    private EmbeddingService embeddingService;

    @Transactional
    public Document processDocument(MultipartFile file) throws IOException {
        logger.info("Processing document: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = documentParsingService.detectContentType(file);
        if (!documentParsingService.isSupportedContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

        // Store file
        String fileName = file.getOriginalFilename();
        String filePath = fileStorageService.storeFile(file, fileName);

        // Create document entity
        Document document = new Document(
                fileName,
                contentType,
                file.getSize(),
                filePath
        );

        // Extract text content
        String content = documentParsingService.extractText(file);
        document.setContent(content);

        // Save document metadata
        document = documentRepository.save(document);

        // Process text chunks and embeddings
        if (content != null && !content.trim().isEmpty()) {
            List<String> chunks = textChunkingService.chunkText(content, fileName);
            document.setChunkCount(chunks.size());

            // Store embeddings
            embeddingService.storeEmbeddings(
                    chunks,
                    document.getId().toString(),
                    document.getFileName()
            );

            // Update document with chunk count
            documentRepository.save(document);
        }

        logger.info("Document processed successfully: {} (ID: {})", fileName, document.getId());
        return document;
    }

    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Document getDocumentById(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    }

    @Transactional
    public void deleteDocument(UUID id) throws IOException {
        Document document = getDocumentById(id);

        // Delete embeddings
        embeddingService.deleteDocumentEmbeddings(id.toString());

        // Delete file
        fileStorageService.deleteFile(document.getFilePath());

        // Delete document record
        documentRepository.delete(document);

        logger.info("Document deleted successfully: {} (ID: {})", document.getFileName(), id);
    }

    @Transactional(readOnly = true)
    public long getDocumentCount() {
        return documentRepository.count();
    }

    @Transactional(readOnly = true)
    public long getProcessedDocumentCount() {
        return documentRepository.countWithContent();
    }
}