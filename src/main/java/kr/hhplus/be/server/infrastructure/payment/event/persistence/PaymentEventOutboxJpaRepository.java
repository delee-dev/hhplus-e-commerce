package kr.hhplus.be.server.infrastructure.payment.event.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface PaymentEventOutboxJpaRepository extends JpaRepository<PaymentEventOutbox, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<PaymentEventOutbox> findByPublishedNotAndCreatedAtBefore(boolean published, LocalDateTime createdAt);
    PaymentEventOutbox findByPaymentId(Long paymentId);
}
