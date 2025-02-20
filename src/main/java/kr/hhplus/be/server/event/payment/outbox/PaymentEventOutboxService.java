package kr.hhplus.be.server.event.payment.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentEventOutboxService {
    private final PaymentEventOutboxRepository paymentEventOutboxRepository;

    @Transactional
    public void save(PaymentEventOutbox paymentEventOutbox) {
        paymentEventOutboxRepository.save(paymentEventOutbox);
    }

    @Transactional
    public List<PaymentEventOutbox> findRetryEventsWithLock() {
        return paymentEventOutboxRepository.findByPublishedNotAndCreatedAtBefore(true, LocalDateTime.now().minusMinutes(5));
    }

    @Transactional
    public void checkPublished(Long paymentId) {
        PaymentEventOutbox paymentEventOutbox = paymentEventOutboxRepository.findByPaymentId(paymentId);
        paymentEventOutbox.checkPublished();
    }

}
