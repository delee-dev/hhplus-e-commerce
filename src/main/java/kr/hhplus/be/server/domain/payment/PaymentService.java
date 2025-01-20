package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(Long orderId, Long totalAmount) {
        return paymentRepository.save(new Payment(orderId, totalAmount));
    }

    @Transactional
    public Payment getPaymentByOrderIdWithLock(Long orderId) {
        return paymentRepository.findByOrderIdWithLock(orderId);
    }
}
