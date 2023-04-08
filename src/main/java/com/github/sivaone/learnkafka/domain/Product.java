package com.github.sivaone.learnkafka.domain;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private String id;
    private String name;
    private String desc;
    private Double price;
}
