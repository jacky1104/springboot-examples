package com.example.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
    "app.kafka.topic-a=topic-a-test",
    "app.kafka.topic-b=topic-b-test"
})
class KafkaIntegrationTest {
  @Container
  static final KafkaContainer KAFKA_A = new KafkaContainer(
      DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

  @Container
  static final KafkaContainer KAFKA_B = new KafkaContainer(
      DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("app.kafka.cluster-a.bootstrap-servers", KAFKA_A::getBootstrapServers);
    registry.add("app.kafka.cluster-b.bootstrap-servers", KAFKA_B::getBootstrapServers);
  }

  @Autowired
  @Qualifier("clusterAKafkaTemplate")
  private org.springframework.kafka.core.KafkaTemplate<String, String> clusterAKafkaTemplate;

  @Autowired
  private MessageConsumer consumer;

  @Autowired
  private KafkaListenerEndpointRegistry registry;

  @BeforeAll
  static void createTopics() throws Exception {
    createTopic(KAFKA_A.getBootstrapServers(), "topic-a-test");
    createTopic(KAFKA_B.getBootstrapServers(), "topic-b-test");
  }

  @Test
  void sendAndConsumeMessage() throws Exception {
    String payload = "hello-kafka";

    ContainerTestUtils.waitForAssignment(registry.getListenerContainer("forwarderListener"), 1);
    ContainerTestUtils.waitForAssignment(registry.getListenerContainer("topicBListener"), 1);

    clusterAKafkaTemplate.send("topic-a-test", payload);

    String received = consumer.getMessages().poll(10, TimeUnit.SECONDS);
    assertThat(received).isEqualTo(payload);
  }

  private static void createTopic(String bootstrapServers, String topic) throws Exception {
    Properties props = new Properties();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    try (AdminClient adminClient = AdminClient.create(props)) {
      adminClient.createTopics(List.of(new NewTopic(topic, 1, (short) 1)))
          .all()
          .get(10, TimeUnit.SECONDS);
    } catch (ExecutionException ex) {
      if (!(ex.getCause() instanceof TopicExistsException)) {
        throw ex;
      }
    }
  }
}
