package com.nayem.ecommerce.kafka;

public record PaymentRequest(
        String requestId,
        com.nayem.ecommerce.payment.PaymentRequest payment
) {
}
