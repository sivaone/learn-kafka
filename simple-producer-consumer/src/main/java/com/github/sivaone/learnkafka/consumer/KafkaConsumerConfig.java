package com.github.sivaone.learnkafka.consumer;

import com.github.sivaone.learnkafka.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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


    // Non blocking Retry configuration
//    @Bean
    public RetryTopicConfiguration myRetryTopic(KafkaTemplate<String, Product> template) {
        return RetryTopicConfigurationBuilder
                .newInstance()
//                .retryOn(IOException.class)
                .fixedBackOff(3000)
                .maxAttempts(4)
                .create(template);
    }

    // Consumer factory example with DeserializationException handling
    // @Bean
    public ConsumerFactory<String, Product> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "127.0.0.1:21345");
        properties.put("group.id", "group");
        properties.put("enable.auto.commit", false);
        properties.put("auto.commit.interval.ms", "10");
        properties.put("session.timeout.ms", "60000");

        ErrorHandlingDeserializer<Product> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(Product.class));
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), errorHandlingDeserializer);
    }


//    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);
        factory.setCommonErrorHandler(errorHandler());

        return factory;
    }

//    @Bean
    KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(ConsumerFactory<String, Product> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Product> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConcurrency(2);
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory);
        kafkaListenerContainerFactory.setCommonErrorHandler(errorHandler());
        return kafkaListenerContainerFactory;
    }
}
