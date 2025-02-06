package com.nayem.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProductTopicConfig {
    @Bean
    public NewTopic purchaseProductsResponseTopic() {
        return TopicBuilder
                .name("purchase-products-response-topic")
                .build();
    }
}
