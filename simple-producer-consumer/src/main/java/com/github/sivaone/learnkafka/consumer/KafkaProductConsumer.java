package com.github.sivaone.learnkafka.consumer;

import com.github.sivaone.learnkafka.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProductConsumer {


    @KafkaListener(topics = {"${app.product.consumer.topic}"})
    public void onMessage(ConsumerRecord<String, Product> productRec) {
        productRec.headers().forEach(h -> log.info(h.key() + " : " + new String(h.value())));
        log.info("Product update key: {}, value: {}", productRec.key(), productRec.value());
    }
}
