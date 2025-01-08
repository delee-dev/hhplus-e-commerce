package kr.hhplus.be.server.infrastructure.order.persistence;

import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemJpaCustomRepository implements OrderItemRepository {
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }
}
