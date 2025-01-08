package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.model.Order;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
}
