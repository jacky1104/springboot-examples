package com.example.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.grpc.client.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GrpcApplicationTests {

    @Autowired
    private GrpcClient grpcClient;

    @Test
    void greetingReturnsExpectedMessage() {
        String message = grpcClient.greet("Test");
        assertThat(message).isEqualTo("Hello, Test!");
    }
}
