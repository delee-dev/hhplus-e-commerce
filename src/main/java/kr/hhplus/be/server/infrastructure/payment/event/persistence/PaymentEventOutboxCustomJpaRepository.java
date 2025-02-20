package kr.hhplus.be.server.infrastructure.payment.event.persistence;

import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutbox;
import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentEventOutboxCustomJpaRepository implements PaymentEventOutboxRepository {
    private final PaymentEventOutboxJpaRepository paymentEventOutboxJpaRepository;

    @Override
    public void save(PaymentEventOutbox paymentEventOutbox) {
        paymentEventOutboxJpaRepository.save(paymentEventOutbox);
    }

    @Override
    public List<PaymentEventOutbox> findByPublishedNotAndCreatedAtBefore(boolean published, LocalDateTime createdAt) {
        return paymentEventOutboxJpaRepository.findByPublishedNotAndCreatedAtBefore(published, createdAt);
    }

    @Override
    public PaymentEventOutbox findByPaymentId(Long paymentId) {
        return paymentEventOutboxJpaRepository.findByPaymentId(paymentId);
    }
}
