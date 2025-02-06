package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.product.ProductPurchaseResponse;
import com.nayem.ecommerce.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseProductsRequestListener {
    private final ProductService productService;
    private final KafkaTemplate<String, PurchaseProductsResponse> kafkaTemplate;

    @KafkaListener(topics = "purchase-products-request-topic")
    public void listenCustomerRequest(PurchaseProductsRequest purchaseProductsRequest) {
        log.info("Received purchase products request: {}", purchaseProductsRequest);

        // Fetch customer details
        List<ProductPurchaseResponse> responses = productService.purchaseProducts(purchaseProductsRequest.products());

        PurchaseProductsResponse purchaseProductsResponse = new PurchaseProductsResponse(
                purchaseProductsRequest.requestId(), responses);

        // Send response back to Order Service
        Message<PurchaseProductsResponse> message = MessageBuilder
                .withPayload(purchaseProductsResponse)
                .setHeader(TOPIC, "purchase-products-response-topic")
                .build();

        kafkaTemplate.send(message);

        log.info("Sent purchase products response: {}", purchaseProductsResponse);
    }
}

