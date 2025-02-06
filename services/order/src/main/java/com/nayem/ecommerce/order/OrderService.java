package com.nayem.ecommerce.order;

import com.nayem.ecommerce.customer.CustomerClient;
import com.nayem.ecommerce.customer.CustomerResponse;
import com.nayem.ecommerce.exceptions.BusinessException;
import com.nayem.ecommerce.kafka.CustomerResponseListener;
import com.nayem.ecommerce.kafka.OrderConfirmation;
import com.nayem.ecommerce.kafka.OrderProducer;
import com.nayem.ecommerce.kafka.PurchaseProductsResponseListener;
import com.nayem.ecommerce.orderline.OrderLineRequest;
import com.nayem.ecommerce.orderline.OrderLineService;
import com.nayem.ecommerce.payment.PaymentClient;
import com.nayem.ecommerce.payment.PaymentRequest;
import com.nayem.ecommerce.product.ProductClient;
import com.nayem.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final CustomerResponseListener customerResponseListener;
    private final PurchaseProductsResponseListener purchaseProductsResponseListener;

    @Transactional
    public Integer createOrder(OrderRequest orderRequest) {
        //Finding customer from customer-service
        var customer = customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        //Purchase product from product-service
        var purchasedProducts = productClient.purchaseProducts(orderRequest.products());

        //Save order
        var order = orderRepository.save(orderMapper.toOrder(orderRequest));

        //Save OrderLine
        for (PurchaseRequest purchaseRequest : orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //Proceed payment to Payment-service
        var paymentRequest = new PaymentRequest(
                orderRequest.amount(),
                orderRequest.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        //Sending message to kafka
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        orderRequest.reference(),
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    @Transactional
    public CompletableFuture<Integer> createOrderEventDriven(OrderRequest orderRequest) {
        // Step 1: Request customer information asynchronously
        return customerResponseListener.getCustomerInfo(orderRequest.customerId())
                .thenCompose(customerResponse -> {
                    var customer = new CustomerResponse(
                            customerResponse.id(), customerResponse.firstname(),
                            customerResponse.lastname(), customerResponse.email()
                    );

                    // Step 2: Request product purchase asynchronously
                    return purchaseProductsResponseListener.getPurchaseProductsInfo(orderRequest.products())
                            .thenApply(purchaseProductsResponse -> {
                                // Step 3: Save order
                                var order = orderRepository.save(orderMapper.toOrder(orderRequest));

                                // Step 4: Save OrderLines
                                for (PurchaseRequest purchaseRequest : orderRequest.products()) {
                                    orderLineService.saveOrderLine(
                                            new OrderLineRequest(
                                                    null, order.getId(),
                                                    purchaseRequest.productId(),
                                                    purchaseRequest.quantity()
                                            )
                                    );
                                }

                                // Step 5: Proceed with payment to Payment-service
                                var paymentRequest = new PaymentRequest(
                                        orderRequest.amount(),
                                        orderRequest.paymentMethod(),
                                        order.getId(),
                                        order.getReference(),
                                        customer
                                );
                                paymentClient.requestOrderPayment(paymentRequest);

                                // Step 6: Send Kafka order confirmation event
                                orderProducer.sendOrderConfirmation(
                                        new OrderConfirmation(
                                                orderRequest.reference(),
                                                orderRequest.amount(),
                                                orderRequest.paymentMethod(),
                                                customer,
                                                purchaseProductsResponse.purchaseProducts()
                                        )
                                );

                                return order.getId();
                            });
                }).exceptionally(exception -> {
                    throw new BusinessException("Cannot create order: " + exception.getMessage());
                });
    }


    public List<OrderResponse> findAllOrders() {
        return this.orderRepository.findAll()
                .stream()
                .map(this.orderMapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.orderRepository.findById(id)
                .map(this.orderMapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}