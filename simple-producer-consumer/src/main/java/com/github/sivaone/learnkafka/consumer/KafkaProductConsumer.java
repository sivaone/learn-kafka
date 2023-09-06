package com.github.sivaone.learnkafka.consumer;

import com.github.sivaone.learnkafka.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProductConsumer {


    @KafkaListener(topics = {"${app.product.consumer.topic}"})
    // Non blocking Retry
   /* @RetryableTopic(
            // kafkaTemplate = "kafkaTemplate",
            // autoCreateTopics = "false",
            exclude = {
                    DeserializationException.class,
                    MessageConversionException.class,
                    ConversionException.class,
                    MethodArgumentResolutionException.class,
                    NoSuchMethodException.class,
                    ClassCastException.class
            },
            attempts = "4",
            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
    )*/
    public void onMessage(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic,
            ConsumerRecord<String, Product> productRec
    ) {
        log.info("Received topic: {}", receivedTopic);
        productRec.headers().forEach(h -> log.info(h.key() + " : " + new String(h.value())));
        log.info("Product update key: {}, value: {}", productRec.key(), productRec.value());
    }


    @DltHandler
    public void processMessage(ConsumerRecord<String, Product> productRec) {
        log.error("Product update key: {}, value: {}", productRec.key(), productRec.value());
    }
}
