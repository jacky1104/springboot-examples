package com.example.grpc.server;

import com.example.grpc.config.GrpcProperties;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class GrpcServerLifecycle implements SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServerLifecycle.class);

    private final GrpcProperties properties;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Server server;

    public GrpcServerLifecycle(GrpcProperties properties) {
        this.properties = properties;
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            try {
                server = NettyServerBuilder.forPort(properties.port())
                        .addService(new GreeterService())
                        .build()
                        .start();
                logger.info("gRPC server started on {}:{}", properties.host(), properties.port());
            } catch (IOException ex) {
                running.set(false);
                throw new IllegalStateException("Failed to start gRPC server", ex);
            }
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false) && server != null) {
            server.shutdown();
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
