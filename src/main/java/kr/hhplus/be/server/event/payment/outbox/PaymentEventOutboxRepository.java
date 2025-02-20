package kr.hhplus.be.server.event.payment.outbox;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentEventOutboxRepository {
    void save(PaymentEventOutbox paymentEventOutbox);
    List<PaymentEventOutbox> findByPublishedNotAndCreatedAtBefore(boolean published, LocalDateTime createdAt);
    PaymentEventOutbox findByPaymentId(Long paymentId);
}
