package kr.hhplus.be.server.infrastructure.order.persistence;

import kr.hhplus.be.server.domain.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}
