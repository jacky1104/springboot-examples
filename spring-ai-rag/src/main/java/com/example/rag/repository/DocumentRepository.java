package com.example.rag.repository;

import com.example.rag.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Optional<Document> findByFileName(String fileName);

    List<Document> findByFileType(String fileType);

    @Query("SELECT d FROM Document d WHERE d.content IS NOT NULL AND d.content != ''")
    List<Document> findAllWithContent();

    @Query("SELECT COUNT(d) FROM Document d WHERE d.content IS NOT NULL AND d.content != ''")
    long countWithContent();
}