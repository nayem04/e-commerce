package com.nayem.ecommerce.kafka;

public record PaymentResponse(
        String requestId,
        Integer paymentId
) {
}