package com.nayem.ecommerce.payment;

import com.nayem.ecommerce.customer.CustomerResponse;
import com.nayem.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}