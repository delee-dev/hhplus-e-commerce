package kr.hhplus.be.server.infrastructure.payment.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.orderId = :orderId")
    Payment findByOrderIdWithLock(@Param("orderId") Long orderId);
    Payment findByOrderId(Long orderId);
}
