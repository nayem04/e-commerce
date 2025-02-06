package com.nayem.ecommerce.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerRequestProducer {
    private final KafkaTemplate<String, CustomerRequest> kafkaTemplate;

    public void sendCustomerRequest(String requestId, String customerId) {
        log.info("Sending customer request");

        CustomerRequest customerRequest = new CustomerRequest(requestId, customerId);

        Message<CustomerRequest> message = MessageBuilder
                .withPayload(customerRequest)
                .setHeader(TOPIC, "customer-request-topic")
                .build();

        kafkaTemplate.send(message);
    }
}