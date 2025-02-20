package kr.hhplus.be.server.scheduler.event;

import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutbox;
import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentEventScheduler {
    private final PaymentEventOutboxService paymentEventOutboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC = "payment-event";

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void retryPublish() {
        List<PaymentEventOutbox> events = paymentEventOutboxService.findRetryEventsWithLock();
        events.forEach(event -> {
            kafkaTemplate.send(TOPIC, event.getPayload());
        });
    }
}
