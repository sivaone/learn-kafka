package com.github.sivaone.learnkafka.api;

import com.github.sivaone.learnkafka.domain.Product;
import com.github.sivaone.learnkafka.producer.KafkaProductProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductUpdateController {

    private final KafkaProductProducer producer;

    public ProductUpdateController(KafkaProductProducer producer) {
        this.producer = producer;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateProduct(@RequestBody Product product) {
        log.info("Product update request received with id: {}", product.getId());
        producer.sendProductUpdate(product);
    }
}
