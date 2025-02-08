package com.nayem.ecommerce.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestProducer {
    private final KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    public void sendPaymentRequest(String requestId, com.nayem.ecommerce.payment.PaymentRequest payment) {
        log.info("Sending payment request");

        PaymentRequest paymentRequest = new PaymentRequest(requestId, payment);

        Message<PaymentRequest> message = MessageBuilder
                .withPayload(paymentRequest)
                .setHeader(TOPIC, "payment-request-topic")
                .build();

        kafkaTemplate.send(message);
    }
}