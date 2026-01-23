package com.example.springboot4redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springboot4redis.service.KeyValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class KeyValueServiceIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    KeyValueService keyValueService;

    @Test
    void shouldSaveFindAndDeleteValueInRedis() {
        String key = "greeting";
        String value = "hello-world";

        // save
        keyValueService.save(key, value);

        // find
        assertThat(keyValueService.find(key)).contains(value);

        // delete
        keyValueService.delete(key);
        assertThat(keyValueService.find(key)).isEmpty();
    }
}
