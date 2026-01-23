package com.example.rag.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    @Test
    void testStoreFile() throws IOException {
        // Create a test file
        String content = "Test file content";
        MockMultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                content.getBytes()
        );

        // Store the file
        String storedPath = fileStorageService.storeFile(file, "test.txt");

        // Verify the file was stored
        assertNotNull(storedPath);
        assertTrue(Files.exists(Path.of(storedPath)));

        // Verify content
        String storedContent = Files.readString(Path.of(storedPath));
        assertEquals(content, storedContent);
    }

    @Test
    void testDeleteFile() throws IOException {
        // Create and store a test file
        MockMultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        String storedPath = fileStorageService.storeFile(file, "test.txt");
        assertTrue(Files.exists(Path.of(storedPath)));

        // Delete the file
        fileStorageService.deleteFile(storedPath);

        // Verify the file was deleted
        assertFalse(Files.exists(Path.of(storedPath)));
    }
}