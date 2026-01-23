package com.example.rag.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {

    private static final Logger logger = LoggerFactory.getLogger(RagService.class);

    @Autowired
    private AnthropicChatModel chatModel;

    @Autowired
    private VectorSearchService vectorSearchService;

    public RagResponse answerQuestion(String question) {
        logger.info("Processing question: {}", question);

        // Step 1: Search for relevant documents
        List<Document> relevantDocs = vectorSearchService.search(question);

        if (relevantDocs.isEmpty()) {
            logger.warn("No relevant documents found for question: {}", question);
            return new RagResponse(
                    "I couldn't find any relevant information in the uploaded documents to answer your question.",
                    List.of()
            );
        }

        // Step 2: Build context from documents
        String context = vectorSearchService.buildContextFromDocuments(relevantDocs);

        // Step 3: Create RAG prompt
        String systemPrompt = """
                You are a helpful AI assistant that answers questions based on the provided context.
                Use the information from the documents to answer the user's question accurately.
                If the information is not available in the context, clearly state that.
                Always cite which documents you're referencing in your answer.
                Be concise but thorough in your responses.
                """;

        String userPrompt = """
                Context:
                %s

                Question: %s

                Please provide an answer based on the context above. If the context doesn't contain
                enough information to answer the question, please state that clearly.
                """.formatted(context, question);

        // Step 4: Generate response using Claude
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(userPrompt));

        Prompt prompt = new Prompt(messages);
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        // Step 5: Extract source documents
        List<String> sourceDocuments = vectorSearchService.extractFileNames(relevantDocs);

        logger.info("Generated response with {} source documents", sourceDocuments.size());

        return new RagResponse(response, sourceDocuments);
    }

    public record RagResponse(String answer, List<String> sourceDocuments) {}
}