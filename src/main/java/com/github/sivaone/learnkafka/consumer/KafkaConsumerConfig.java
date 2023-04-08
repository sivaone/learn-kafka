package com.github.sivaone.learnkafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler() {

        return new DefaultErrorHandler(
                (record, exception) -> log.error(record.key().toString(), exception),
                new FixedBackOff(0L, 2L)
        );
    }
}
