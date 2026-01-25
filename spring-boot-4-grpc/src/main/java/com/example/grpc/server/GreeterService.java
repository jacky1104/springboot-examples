package com.example.grpc.server;

import com.example.grpc.proto.GreeterGrpc;
import com.example.grpc.proto.HelloReply;
import com.example.grpc.proto.HelloRequest;
import io.grpc.stub.StreamObserver;

public class GreeterService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        String message = "Hello, " + request.getName() + "!";
        HelloReply reply = HelloReply.newBuilder()
                .setMessage(message)
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
