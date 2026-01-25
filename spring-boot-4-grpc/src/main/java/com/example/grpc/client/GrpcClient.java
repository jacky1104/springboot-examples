package com.example.grpc.client;

import com.example.grpc.config.GrpcProperties;
import com.example.grpc.proto.GreeterGrpc;
import com.example.grpc.proto.HelloReply;
import com.example.grpc.proto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class GrpcClient {

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub stub;

    public GrpcClient(GrpcProperties properties) {
        this.channel = ManagedChannelBuilder.forAddress(properties.host(), properties.port())
                .usePlaintext()
                .build();
        this.stub = GreeterGrpc.newBlockingStub(channel);
    }

    public String greet(String name) {
        HelloReply reply = stub.sayHello(HelloRequest.newBuilder().setName(name).build());
        return reply.getMessage();
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
