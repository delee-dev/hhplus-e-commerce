package kr.hhplus.be.server.event.payment.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.event.payment.model.PaymentCompletedEvent;
import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutbox;
import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentEventOutboxService paymentEventOutboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final String TOPIC = "payment-event";

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveToOutbox(PaymentCompletedEvent event) {
        try {
            paymentEventOutboxService.save(PaymentEventOutbox.from(event, objectMapper.writeValueAsString(event)));
        } catch (JsonProcessingException e) {
            // 메인 비즈니스는 롤백 X
            log.error("직렬화 중 예외 발생 paymentId={}", event.getPaymentId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(PaymentCompletedEvent event) {
        try {
            kafkaTemplate.send(TOPIC, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            //
            log.error("직렬화 중 예외 발생 paymentId={}", event.getPaymentId());
        }
    }

    @KafkaListener(topics = "payment-event", groupId = TOPIC)
    public void checkPublish(String payload) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(payload, PaymentCompletedEvent.class);
            paymentEventOutboxService.checkPublished(event.getPaymentId());
        } catch (JsonProcessingException e) {
            // 메인 비즈니스는 롤백 X
            log.error("역직렬화 중 예외 발생, payload={}", payload);
        }
    }
}
