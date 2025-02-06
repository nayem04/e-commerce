package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.product.PurchaseResponse;

import java.util.List;

public record PurchaseProductsResponse(
        String requestId,
        List<PurchaseResponse> purchaseProducts
) {
}