package com.example.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topicB;

  public MessageProducer(@Qualifier("clusterBKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
                         @Value("${app.kafka.topic-b}") String topicB) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicB = topicB;
  }

  public void sendToTopicB(String message) {
    kafkaTemplate.send(topicB, message);
  }
}
