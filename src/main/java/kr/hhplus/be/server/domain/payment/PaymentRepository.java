package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.model.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
