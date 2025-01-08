package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.CreateOrderCommand;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order createOrder(CreateOrderCommand command) {
        Order order = new Order(
                command.userId(),
                command.receiverName(),
                command.receiverPhone(),
                command.shippingAddress()
        );
        List<OrderItem> orderItems = command.orderItems().stream().map(itemCommand -> {
            return new OrderItem(
                    order,
                    itemCommand.productId(),
                    itemCommand.productName(),
                    itemCommand.price(),
                    itemCommand.quantity()
            );
        }).toList();

        order.changeOrderItems(orderItems);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return order;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
