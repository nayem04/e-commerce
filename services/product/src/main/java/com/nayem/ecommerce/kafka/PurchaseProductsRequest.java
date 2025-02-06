package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.product.ProductPurchaseRequest;

import java.util.List;

public record PurchaseProductsRequest(
        String requestId,
        List<ProductPurchaseRequest> products
) {
}
