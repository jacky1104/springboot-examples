package com.example.kafka;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
  private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();

  @KafkaListener(
      topics = "${app.kafka.topic-b}",
      groupId = "demo-consumer",
      containerFactory = "clusterBKafkaListenerContainerFactory",
      id = "topicBListener")
  public void consumeFromTopicB(String message) {
    messages.offer(message);
  }

  public BlockingQueue<String> getMessages() {
    return messages;
  }
}
