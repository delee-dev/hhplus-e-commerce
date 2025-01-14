package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.model.Payment;

import java.time.LocalDateTime;

public record OrderResult(
        long orderId,
        long paymentId,
        OrderStatus orderStatus,
        LocalDateTime orderTime
) {
    public static OrderResult from(Order order, Payment payment) {
        return new OrderResult(
                order.getId(),
                payment.getId(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
