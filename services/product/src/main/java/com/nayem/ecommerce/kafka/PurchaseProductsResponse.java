package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.product.ProductPurchaseResponse;

import java.util.List;

public record PurchaseProductsResponse(
        String requestId,
        List<ProductPurchaseResponse> purchaseProducts
) {
}