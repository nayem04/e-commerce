package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.customer.Customer;
import com.nayem.ecommerce.customer.CustomerRepository;
import com.nayem.ecommerce.exceptions.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerRequestListener {
    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, CustomerResponse> kafkaTemplate;

    @KafkaListener(topics = "customer-request-topic")
    public void listenCustomerRequest(CustomerRequest customerRequest) {
        log.info("Received customer request: {}", customerRequest);

        // Fetch customer details
        Customer customer = customerRepository.findById(customerRequest.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with the provided ID: %s", customerRequest.customerId())));

        CustomerResponse customerResponse = new CustomerResponse(
                customerRequest.requestId(), customer.getId(), customer.getFirstname(),
                customer.getLastname(), customer.getEmail());

        // Send response back to Order Service
        Message<CustomerResponse> message = MessageBuilder
                .withPayload(customerResponse)
                .setHeader(TOPIC, "customer-response-topic")
                .build();

        kafkaTemplate.send(message);

        log.info("Sent customer response: {}", customerResponse);
    }
}

