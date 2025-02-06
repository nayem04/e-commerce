package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.product.PurchaseRequest;

import java.util.List;

public record PurchaseProductsRequest(
        String requestId,
        List<PurchaseRequest> products
) {
}
