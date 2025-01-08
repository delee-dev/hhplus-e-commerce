package kr.hhplus.be.server.infrastructure.order.persistence;

import kr.hhplus.be.server.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
