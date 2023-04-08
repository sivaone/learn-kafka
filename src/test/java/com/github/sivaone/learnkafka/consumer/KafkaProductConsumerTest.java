package com.github.sivaone.learnkafka.consumer;

import com.github.sivaone.learnkafka.domain.Product;
import com.github.sivaone.learnkafka.producer.KafkaProductProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Disabled
//@ExtendWith(SpringExtension.class)
//@EmbeddedKafka(partitions = 3, topics = {"products"})
class KafkaProductConsumerTest {

    public static final String TOPIC_NAME = "products";
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaTemplate<String, Product> kafkaTemplate;

    @Disabled
    @Test
    void testSendProduct() {
        KafkaProductProducer producer = new KafkaProductProducer(kafkaTemplate, TOPIC_NAME);
        producer.sendProductUpdate(new Product("abc123", "test product", "test desc", 100.00));

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testT", "false",
                embeddedKafkaBroker);
        DefaultKafkaConsumerFactory<String, Product> cf =
                new DefaultKafkaConsumerFactory<>(consumerProps);
        ContainerProperties containerProperties = new ContainerProperties(TOPIC_NAME);
        KafkaMessageListenerContainer<String, Product> container =
                new KafkaMessageListenerContainer<>(cf, containerProperties);
        final BlockingQueue<ConsumerRecord<String, Product>> records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, Product>) record -> {
            System.out.println(record);
            records.add(record);
        });
        container.setBeanName("templateTests");
        container.start();

    }
}