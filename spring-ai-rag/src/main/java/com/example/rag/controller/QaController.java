package com.example.rag.controller;

import com.example.rag.service.RagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qa")
@CrossOrigin(origins = "*")
public class QaController {

    private static final Logger logger = LoggerFactory.getLogger(QaController.class);

    @Autowired
    private RagService ragService;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody QuestionRequest request) {
        logger.info("Question received: {}", request.question());

        if (request.question() == null || request.question().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Question cannot be empty"));
        }

        try {
            RagService.RagResponse response = ragService.answerQuestion(request.question());

            Map<String, Object> result = new HashMap<>();
            result.put("question", request.question());
            result.put("answer", response.answer());
            result.put("sourceDocuments", response.sourceDocuments());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error processing question: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process question"));
        }
    }

    public record QuestionRequest(String question) {}
}