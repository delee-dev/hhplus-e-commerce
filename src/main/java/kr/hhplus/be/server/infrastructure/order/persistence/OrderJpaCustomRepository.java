package kr.hhplus.be.server.infrastructure.order.persistence;

import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderJpaCustomRepository implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }
}
