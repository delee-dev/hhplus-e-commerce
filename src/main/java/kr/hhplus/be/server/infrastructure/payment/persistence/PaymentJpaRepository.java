package kr.hhplus.be.server.infrastructure.payment.persistence;

import kr.hhplus.be.server.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
