package kr.hhplus.be.server.api.order.dto;

import java.util.List;

public record OrderRequest(
        long userId,
        List<OrderItemRequest> orderItems,
        String receiverName,
        String receiverPhone,
        String shippingAddress
) {
}
