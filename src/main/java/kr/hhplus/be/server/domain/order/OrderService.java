package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.CreateOrderCommand;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public Order createOrder(CreateOrderCommand command) {
        List<OrderItem> orderItems = command.orderItems().stream().map(itemCommand -> {
            return new OrderItem(
                    itemCommand.productId(),
                    itemCommand.productName(),
                    itemCommand.price(),
                    itemCommand.quantity()
            );
        }).toList();
        Order order = new Order(
                command.userId(),
                orderItems,
                command.receiverName(),
                command.receiverPhone(),
                command.shippingAddress()
        );

        return orderRepository.save(order);
    }

    public Order completePayment(Long orderId) {
        Order order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
        order.completePay();
        return order;
    }
}
