package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseProductsRequestProducer {
    private final KafkaTemplate<String, PurchaseProductsRequest> kafkaTemplate;

    public void sendPurchaseProductsRequest(String requestId, List<PurchaseRequest> products) {
        log.info("Sending purchase product request");

        PurchaseProductsRequest purchaseProductsRequest = new PurchaseProductsRequest(requestId, products);

        Message<PurchaseProductsRequest> message = MessageBuilder
                .withPayload(purchaseProductsRequest)
                .setHeader(TOPIC, "purchase-products-request-topic")
                .build();

        kafkaTemplate.send(message);
    }
}