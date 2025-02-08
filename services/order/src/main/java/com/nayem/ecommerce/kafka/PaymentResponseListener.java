package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.payment.PaymentRequest;
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
public class PaymentResponseListener {
    private final PaymentRequestProducer paymentRequestProducer;
    private final Map<String, CompletableFuture<PaymentResponse>> responseMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "payment-response-topic")
    public void listenPaymentResponse(PaymentResponse paymentResponse) {
        log.info("Received purchase products response: {}", paymentResponse);

        // Check if we are waiting for this response
        CompletableFuture<PaymentResponse> paymentResponseCompletableFuture =
                responseMap.remove(paymentResponse.requestId());
        if (paymentResponseCompletableFuture != null) {
            paymentResponseCompletableFuture.complete(paymentResponse);  // Complete the future so the caller gets the response
        }
    }

    public CompletableFuture<PaymentResponse> proceedPayment(PaymentRequest payment) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<PaymentResponse> paymentResponseCompletableFuture = new CompletableFuture<>();

        responseMap.put(requestId, paymentResponseCompletableFuture);

        // Send Kafka request
        paymentRequestProducer.sendPaymentRequest(requestId, payment);

        return paymentResponseCompletableFuture; // Caller will wait for response asynchronously
    }
}
