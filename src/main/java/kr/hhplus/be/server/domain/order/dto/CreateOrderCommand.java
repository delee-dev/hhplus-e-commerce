package kr.hhplus.be.server.domain.order.dto;

import java.util.List;

public record CreateOrderCommand(
        Long userId,
        List<CreateOrderItemCommand> orderItems,
        String receiverName,
        String receiverPhone,
        String shippingAddress
) {
}
