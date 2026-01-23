package com.example.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaConfig {
  @Bean
  public ConsumerFactory<String, String> clusterAConsumerFactory(
      @Value("${app.kafka.cluster-a.bootstrap-servers}") String bootstrapServers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConsumerFactory<String, String> clusterBConsumerFactory(
      @Value("${app.kafka.cluster-b.bootstrap-servers}") String bootstrapServers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String>
      clusterAKafkaListenerContainerFactory(
          @Qualifier("clusterAConsumerFactory") ConsumerFactory<String, String> clusterAConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(clusterAConsumerFactory);
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String>
      clusterBKafkaListenerContainerFactory(
          @Qualifier("clusterBConsumerFactory") ConsumerFactory<String, String> clusterBConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(clusterBConsumerFactory);
    return factory;
  }

  @Bean
  public ProducerFactory<String, String> clusterAProducerFactory(
      @Value("${app.kafka.cluster-a.bootstrap-servers}") String bootstrapServers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public ProducerFactory<String, String> clusterBProducerFactory(
      @Value("${app.kafka.cluster-b.bootstrap-servers}") String bootstrapServers) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, String> clusterAKafkaTemplate(
      @Qualifier("clusterAProducerFactory") ProducerFactory<String, String> clusterAProducerFactory) {
    return new KafkaTemplate<>(clusterAProducerFactory);
  }

  @Bean
  public KafkaTemplate<String, String> clusterBKafkaTemplate(
      @Qualifier("clusterBProducerFactory") ProducerFactory<String, String> clusterBProducerFactory) {
    return new KafkaTemplate<>(clusterBProducerFactory);
  }
}
