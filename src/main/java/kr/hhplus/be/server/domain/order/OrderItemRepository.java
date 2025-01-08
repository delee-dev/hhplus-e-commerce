package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {
    List<OrderItem> saveAll(List<OrderItem> orderItems);
}
