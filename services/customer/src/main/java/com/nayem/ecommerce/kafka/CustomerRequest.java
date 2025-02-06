package com.nayem.ecommerce.kafka;

public record CustomerRequest(
        String requestId,
        String customerId
) {
}
