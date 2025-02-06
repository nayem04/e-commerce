package com.nayem.ecommerce.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerResponseListener {
    private final CustomerRequestProducer customerRequestProducer;
    private final Map<String, CompletableFuture<CustomerResponse>> responseMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "customer-response-topic")
    public void listenCustomerResponse(CustomerResponse customerResponse) {
        log.info("Received customer response: {}", customerResponse);

        // Check if we are waiting for this response
        CompletableFuture<CustomerResponse> customerResponseCompletableFuture = responseMap.remove(customerResponse.requestId());
        if (customerResponseCompletableFuture != null) {
            customerResponseCompletableFuture.complete(customerResponse);  // Complete the future so the caller gets the response
        }
    }

    public CompletableFuture<CustomerResponse> getCustomerInfo(String customerId) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<CustomerResponse> customerResponseCompletableFuture = new CompletableFuture<>();

        responseMap.put(requestId, customerResponseCompletableFuture);

        // Send Kafka request
        customerRequestProducer.sendCustomerRequest(requestId, customerId);

        return customerResponseCompletableFuture; // Caller will wait for response asynchronously
    }
}
