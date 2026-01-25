package com.example.grpc.web;

import com.example.grpc.client.GrpcClient;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrpcController {

    private final GrpcClient grpcClient;

    public GrpcController(GrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }

    @GetMapping("/api/greet")
    public Map<String, String> greet(@RequestParam(defaultValue = "Spring") String name) {
        return Map.of("message", grpcClient.greet(name));
    }
}
