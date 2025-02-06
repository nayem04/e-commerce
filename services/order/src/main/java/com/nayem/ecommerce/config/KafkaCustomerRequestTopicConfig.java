package com.nayem.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaCustomerRequestTopicConfig {
    @Bean
    public NewTopic customerRequestTopic() {
        return TopicBuilder
                .name("customer-request-topic")
                .build();
    }
}
