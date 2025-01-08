package kr.hhplus.be.server.infrastructure.payment.persistence;

import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentJpaCustomRepository implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment getPaymentByOrderIdWithLock(Long orderId) {
        return paymentJpaRepository.findByOrderId(orderId);
    }
}
