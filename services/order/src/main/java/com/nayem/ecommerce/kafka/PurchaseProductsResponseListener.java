package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseProductsResponseListener {
    private final PurchaseProductsRequestProducer purchaseProductRequestProducer;
    private final Map<String, CompletableFuture<PurchaseProductsResponse>> responseMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "purchase-products-response-topic")
    public void listenPurchaseProductsResponse(PurchaseProductsResponse purchaseProductsResponse) {
        log.info("Received purchase products response: {}", purchaseProductsResponse);

        // Check if we are waiting for this response
        CompletableFuture<PurchaseProductsResponse> purchaseProductsResponseCompletableFuture =
                responseMap.remove(purchaseProductsResponse.requestId());
        if (purchaseProductsResponseCompletableFuture != null) {
            purchaseProductsResponseCompletableFuture.complete(purchaseProductsResponse);  // Complete the future so the caller gets the response
        }
    }

    public CompletableFuture<PurchaseProductsResponse> getPurchaseProductsInfo(List<PurchaseRequest> products) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<PurchaseProductsResponse> purchaseProductsResponseCompletableFuture = new CompletableFuture<>();

        responseMap.put(requestId, purchaseProductsResponseCompletableFuture);

        // Send Kafka request
        purchaseProductRequestProducer.sendPurchaseProductsRequest(requestId, products);

        return purchaseProductsResponseCompletableFuture; // Caller will wait for response asynchronously
    }
}
