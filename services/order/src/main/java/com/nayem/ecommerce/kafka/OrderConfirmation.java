package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.customer.CustomerResponse;
import com.nayem.ecommerce.order.PaymentMethod;
import com.nayem.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}