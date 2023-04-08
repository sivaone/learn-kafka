package com.github.sivaone.learnkafka.producer;

import com.github.sivaone.learnkafka.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaProductProducer {
    private final KafkaTemplate<String, Product> kafkaTemplate;
    private final String topic;

    public KafkaProductProducer(
            KafkaTemplate<String, Product> kafkaTemplate,
            @Value("${app.product.producer.topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Transactional
    public void sendProductUpdate(final Product product) {
        log.info("Sending msg to kafka topic");
        CompletableFuture<SendResult<String, Product>> result = kafkaTemplate.send(topic, product.getId(), product);
        result.thenAccept(e -> log.info(e.toString()));
    }
}
