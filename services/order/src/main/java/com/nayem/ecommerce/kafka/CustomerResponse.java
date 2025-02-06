package com.nayem.ecommerce.kafka;

public record CustomerResponse(
        String requestId,
        String id,
        String firstname,
        String lastname,
        String email
) {
}