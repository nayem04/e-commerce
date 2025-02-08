package com.nayem.ecommerce.kafka;

import com.nayem.ecommerce.payment.PaymentService;
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
public class PaymentRequestListener {
    private final PaymentService paymentService;
    private final KafkaTemplate<String, PaymentResponse> kafkaTemplate;

    @KafkaListener(topics = "payment-request-topic")
    public void listenPaymentRequest(PaymentRequest paymentRequest) {
        log.info("Received payment request: {}", paymentRequest);

        Integer paymentId = paymentService.createPayment(paymentRequest.payment());

        PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.requestId(), paymentId);

        // Send response back to Order Service
        Message<PaymentResponse> message = MessageBuilder
                .withPayload(paymentResponse)
                .setHeader(TOPIC, "payment-response-topic")
                .build();

        kafkaTemplate.send(message);

        log.info("Sent payment response: {}", paymentResponse);
    }
}

