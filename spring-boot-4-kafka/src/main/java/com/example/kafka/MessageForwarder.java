package com.example.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageForwarder {
  private final MessageProducer producer;

  public MessageForwarder(MessageProducer producer) {
    this.producer = producer;
  }

  @KafkaListener(
      topics = "${app.kafka.topic-a}",
      groupId = "forwarder-group",
      containerFactory = "clusterAKafkaListenerContainerFactory",
      id = "forwarderListener")
  public void forward(String message) {
    producer.sendToTopicB(message);
  }
}
