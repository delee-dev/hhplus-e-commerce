package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment createPayment(Long orderId, Long totalAmount) {
        return paymentRepository.save(new Payment(orderId, totalAmount));
    }

    public Payment getPaymentByOrderIdWithLock(Long orderId) {
        return paymentRepository.getPaymentByOrderIdWithLock(orderId);
    }
}
