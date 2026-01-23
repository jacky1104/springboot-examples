package com.example.rag.config;

import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingModelConfig {

    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        return new TransformersEmbeddingModel();
    }
}