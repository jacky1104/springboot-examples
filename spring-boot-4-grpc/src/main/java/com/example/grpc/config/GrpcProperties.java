package com.example.grpc.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "grpc")
public record GrpcProperties(
        @NotBlank String host,
        @Positive int port
) {
}
