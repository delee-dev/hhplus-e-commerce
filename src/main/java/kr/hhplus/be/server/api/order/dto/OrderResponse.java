package kr.hhplus.be.server.api.order.dto;

import kr.hhplus.be.server.domain.order.model.OrderStatus;

import java.time.LocalDateTime;

public record OrderResponse(
        long orderId,
        long paymentId,
        OrderStatus orderStatus,
        LocalDateTime orderTime
) {
}
