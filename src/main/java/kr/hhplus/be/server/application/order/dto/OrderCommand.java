package kr.hhplus.be.server.application.order.dto;

import java.util.List;

public record OrderCommand(
        long userId,
        List<OrderItemCommand> orderItems,
        String receiverName,
        String receiverPhone,
        String shippingAddress
) {
}
